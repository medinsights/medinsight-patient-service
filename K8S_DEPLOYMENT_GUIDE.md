# Patient Service - Kubernetes & ArgoCD Deployment Guide

## 📁 Project Structure

```
medinsight-patient-service/
├── Dockerfile                  # Multi-stage Docker build
├── .dockerignore              # Docker build exclusions
├── docker-compose.yml         # Local development
├── k8s/
│   ├── base/                  # Base K8s manifests (environment-agnostic)
│   │   ├── namespace.yaml         # Namespace definition
│   │   ├── configmap.yaml         # Application configuration
│   │   ├── secret.yaml            # Sensitive data (credentials)
│   │   ├── postgres-statefulset.yaml  # PostgreSQL database
│   │   ├── deployment.yaml        # Patient service deployment
│   │   ├── service.yaml           # ClusterIP service
│   │   └── kustomization.yaml     # Kustomize base config
│   └── overlays/              # Environment-specific overrides
│       ├── dev/
│       │   ├── kustomization.yaml     # Dev environment config
│       │   └── deployment-patch.yaml  # Resource limits for dev
│       └── prod/
│           ├── kustomization.yaml     # Prod environment config
│           └── deployment-patch.yaml  # Resource limits for prod
└── argocd/
    └── application.yaml       # ArgoCD Application manifest
```

---

## 🐳 Docker Setup Explanation

### **Dockerfile - Multi-Stage Build**

```dockerfile
# Stage 1: Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder
- Uses Java 21 JDK to compile the application
- Downloads Maven dependencies (cached layer for faster rebuilds)
- Builds the JAR file with `mvnw clean package`

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine
- Uses lightweight JRE (smaller image size)
- Runs as non-root user (security best practice)
- Includes health check for container orchestration
- Optimized JVM settings for containerized environments
```

**Benefits:**
- ✅ Small image size (~200MB vs 500MB+)
- ✅ Faster deployments
- ✅ Security: no build tools in production image
- ✅ Layer caching speeds up rebuilds

---

## ☸️ Kubernetes Manifests Explanation

### **1. Namespace** (`namespace.yaml`)
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: medinsights
```
- **Purpose:** Isolates resources for the MedInsights project
- **Why:** Prevents naming conflicts, enables resource quotas, and provides logical separation

---

### **2. ConfigMap** (`configmap.yaml`)
```yaml
apiVersion: v1
kind: ConfigMap
data:
  SPRING_DATASOURCE_URL: "jdbc:postgresql://postgres-service:5432/patient_db"
  SERVER_PORT: "8080"
```
- **Purpose:** Stores non-sensitive configuration
- **Benefits:**
  - Changes don't require rebuilding Docker images
  - Environment-specific configs without code changes
  - Can be updated without redeploying pods (with proper reload mechanisms)

---

### **3. Secret** (`secret.yaml`)
```yaml
apiVersion: v1
kind: Secret
stringData:
  SPRING_DATASOURCE_USERNAME: patient_user
  SPRING_DATASOURCE_PASSWORD: patient_pass
```
- **Purpose:** Stores sensitive data (passwords, tokens, keys)
- **Security Notes:**
  - ⚠️ Base64 encoded (NOT encrypted)
  - For production, use:
    - **Sealed Secrets** (encrypted in Git)
    - **External Secrets Operator** (sync from Vault/AWS Secrets Manager)
    - **HashiCorp Vault** integration

---

### **4. StatefulSet for PostgreSQL** (`postgres-statefulset.yaml`)

```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: postgres
spec:
  serviceName: postgres-service
  volumeClaimTemplates:
    - metadata:
        name: postgres-storage
      spec:
        accessModes: ["ReadWriteOnce"]
        resources:
          requests:
            storage: 5Gi
```

**Why StatefulSet instead of Deployment?**
- ✅ **Stable network identity:** Pods get predictable names (postgres-0, postgres-1)
- ✅ **Persistent storage:** Each pod gets its own PersistentVolumeClaim
- ✅ **Ordered deployment:** Pods start/stop in order
- ✅ **Data persistence:** Database data survives pod restarts

**Components:**
1. **Headless Service** (`clusterIP: None`): Direct pod-to-pod communication
2. **VolumeClaimTemplate**: Auto-creates PersistentVolumeClaims for each pod
3. **Probes**:
   - `livenessProbe`: Restarts unhealthy pods
   - `readinessProbe`: Removes unready pods from service endpoints

---

### **5. Deployment** (`deployment.yaml`)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: patient-service
spec:
  replicas: 2
  template:
    spec:
      initContainers:
        - name: wait-for-postgres
          # Waits for PostgreSQL to be ready
      containers:
        - name: patient-service
          image: patient-service:latest
          resources:
            requests:
              memory: "512Mi"
              cpu: "500m"
            limits:
              memory: "1Gi"
              cpu: "1000m"
```

**Key Features:**
- **Replicas:** 2 pods for high availability
- **InitContainer:** Waits for PostgreSQL before starting the app
- **Resource Requests/Limits:**
  - `requests`: Minimum guaranteed resources (used for scheduling)
  - `limits`: Maximum allowed resources (prevents resource hogging)
- **Probes:**
  - `startupProbe`: Gives slow-starting apps extra time
  - `livenessProbe`: Restarts crashed/frozen pods
  - `readinessProbe`: Controls traffic routing

---

### **6. Service** (`service.yaml`)

```yaml
apiVersion: v1
kind: Service
metadata:
  name: patient-service
spec:
  type: ClusterIP
  selector:
    app: patient-service
  ports:
    - port: 8080
      targetPort: 8080
```

**Service Type: ClusterIP**
- **Why:** Internal-only access (not exposed to the internet)
- **Purpose:** Kong Gateway will route traffic to this service
- **DNS:** Accessible at `patient-service.medinsights.svc.cluster.local:8080`

---

## 🔧 Kustomize Explanation

**Kustomize** manages environment-specific configurations without duplicating YAML files.

### **Base Configuration** (`k8s/base/kustomization.yaml`)
```yaml
resources:
  - namespace.yaml
  - configmap.yaml
  - secret.yaml
  - postgres-statefulset.yaml
  - deployment.yaml
  - service.yaml

images:
  - name: patient-service
    newName: patient-service
    newTag: latest
```
- Contains the common configuration shared across all environments

### **Dev Overlay** (`k8s/overlays/dev/kustomization.yaml`)
```yaml
bases:
  - ../../base

namePrefix: dev-
namespace: medinsights-dev

patches:
  - patch: |-
      - op: replace
        path: /spec/replicas
        value: 1
    target:
      kind: Deployment
      name: patient-service

images:
  - name: patient-service
    newTag: dev-latest
```
- **Overrides:**
  - 1 replica (dev doesn't need HA)
  - Lower resource limits
  - Different namespace
  - Dev-specific image tag

### **Prod Overlay** (`k8s/overlays/prod/kustomization.yaml`)
```yaml
replicas:
  - name: patient-service
    count: 3

images:
  - name: patient-service
    newTag: v0.0.1
```
- **Overrides:**
  - 3 replicas for high availability
  - Higher resource limits
  - Specific version tag (not `latest`)

---

## 🚀 ArgoCD Application Manifest

```yaml
apiVersion: argoproj.io/v1alpha1
kind: Application
metadata:
  name: patient-service
  namespace: argocd
spec:
  source:
    repoURL: https://github.com/YOUR_ORG/medinsight-patient-service.git
    targetRevision: main
    path: k8s/base

  destination:
    server: https://kubernetes.default.svc
    namespace: medinsights

  syncPolicy:
    automated:
      prune: true       # Delete resources removed from Git
      selfHeal: true    # Auto-sync on drift
```

### **ArgoCD Concepts:**

1. **GitOps Workflow:**
   ```
   Git Repo → ArgoCD monitors → Detects changes → Syncs to K8s
   ```

2. **Automated Sync Policies:**
   - `prune: true`: Removes resources deleted from Git
   - `selfHeal: true`: Corrects manual kubectl changes
   - `CreateNamespace=true`: Auto-creates the namespace

3. **Deployment Flow:**
   ```
   Developer → git push → ArgoCD detects → Applies manifests → Health checks
   ```

---

## 📝 Step-by-Step Deployment

### **Step 1: Build and Push Docker Image**

```bash
# Navigate to patient service
cd medinsight-patient-service

# Build Docker image
docker build -t your-registry.io/medinsights/patient-service:v0.0.1 .

# Push to registry
docker push your-registry.io/medinsights/patient-service:v0.0.1
```

### **Step 2: Update Image in Kustomize**

Edit `k8s/base/kustomization.yaml`:
```yaml
images:
  - name: patient-service
    newName: your-registry.io/medinsights/patient-service
    newTag: v0.0.1
```

### **Step 3: Test Manifests Locally**

```bash
# Preview rendered manifests
kubectl kustomize k8s/base

# Apply to cluster
kubectl apply -k k8s/base

# Check deployment
kubectl get pods -n medinsights
kubectl logs -f deployment/patient-service -n medinsights
```

### **Step 4: Deploy with ArgoCD**

```bash
# Install ArgoCD (if not installed)
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# Apply ArgoCD application
kubectl apply -f argocd/application.yaml

# Access ArgoCD UI
kubectl port-forward svc/argocd-server -n argocd 8080:443

# Get admin password
kubectl get secret argocd-initial-admin-secret -n argocd -o jsonpath="{.data.password}" | base64 -d
```

### **Step 5: Push to Git Repository**

```bash
# Initialize git repository
cd medinsight-patient-service
git init
git add .
git commit -m "Add Kubernetes manifests and ArgoCD config"

# Add remote and push
git remote add origin https://github.com/YOUR_ORG/medinsight-patient-service.git
git push -u origin main
```

### **Step 6: Verify Deployment**

```bash
# Check ArgoCD sync status
kubectl get application -n argocd

# Check pods
kubectl get pods -n medinsights

# Check service
kubectl get svc -n medinsights

# Test service endpoint
kubectl port-forward svc/patient-service -n medinsights 8080:8080
curl http://localhost:8080/actuator/health
```

---

## 🔄 CI/CD Pipeline Integration

### **GitHub Actions Example** (`.github/workflows/deploy.yml`)

```yaml
name: Build and Deploy

on:
  push:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Build Docker image
        run: docker build -t ${{ secrets.REGISTRY }}/patient-service:${{ github.sha }} .

      - name: Push to registry
        run: docker push ${{ secrets.REGISTRY }}/patient-service:${{ github.sha }}

      - name: Update Kustomize image
        run: |
          cd k8s/base
          kustomize edit set image patient-service=${{ secrets.REGISTRY }}/patient-service:${{ github.sha }}

      - name: Commit and push
        run: |
          git config user.name "GitHub Actions"
          git add k8s/base/kustomization.yaml
          git commit -m "Update image to ${{ github.sha }}"
          git push
```

---

## 🌐 Kong Gateway Integration (Next Step)

Your architecture requires Kong Gateway to route traffic to services:

```
User → Kong Gateway → Patient Service (ClusterIP)
                   → Auth Service (ClusterIP)
```

**Kong Configuration (Next Step):**
1. Deploy Kong to K8s
2. Create Kong Ingress resources
3. Configure JWT authentication
4. Set up rate limiting

---

## 🔐 Production Best Practices

1. **Secrets Management:**
   - Use External Secrets Operator or Sealed Secrets
   - Never commit secrets to Git

2. **Resource Limits:**
   - Always set requests and limits
   - Use Vertical Pod Autoscaler (VPA) to tune values

3. **Monitoring:**
   - Add Prometheus annotations
   - Set up Grafana dashboards
   - Configure alerting

4. **Security:**
   - Use non-root containers ✅
   - Enable Pod Security Standards
   - Network policies to restrict traffic
   - Image scanning (Trivy, Snyk)

5. **High Availability:**
   - Multi-replica deployments ✅
   - Pod Disruption Budgets
   - Anti-affinity rules (spread across nodes)

---

## 📊 Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    Multi-Repo Setup                      │
│                                                           │
│  ┌─────────────────┐  ┌─────────────────┐              │
│  │  Patient Repo   │  │   Auth Repo     │              │
│  │  ├── src/       │  │   ├── src/      │              │
│  │  ├── k8s/       │  │   ├── k8s/      │              │
│  │  └── argocd/    │  │   └── argocd/   │              │
│  └─────────────────┘  └─────────────────┘              │
│           │                      │                        │
│           └──────────┬───────────┘                       │
│                      ▼                                    │
│              ┌──────────────┐                            │
│              │   ArgoCD     │                            │
│              │ (GitOps)     │                            │
│              └──────┬───────┘                            │
│                     ▼                                    │
│         ┌───────────────────────┐                       │
│         │   Kubernetes Cluster   │                       │
│         │                        │                       │
│         │  ┌─────────────────┐  │                       │
│         │  │  Kong Gateway   │  │ ← Ingress             │
│         │  └────────┬────────┘  │                       │
│         │           │            │                       │
│         │  ┌────────▼────────┐  │                       │
│         │  │ Patient Service │  │ (ClusterIP)           │
│         │  │   + PostgreSQL  │  │                       │
│         │  └─────────────────┘  │                       │
│         │                        │                       │
│         │  ┌─────────────────┐  │                       │
│         │  │  Auth Service   │  │ (ClusterIP)           │
│         │  │   + Database    │  │                       │
│         │  └─────────────────┘  │                       │
│         └────────────────────────┘                       │
└─────────────────────────────────────────────────────────┘
```

---

## ✅ Next Steps

1. ✅ **Patient Service:** Dockerized and K8s manifests ready
2. ⏭️  **Auth Service:** Repeat similar setup
3. ⏭️  **Kong Gateway:** Deploy and configure routing
4. ⏭️  **Frontend:** Deploy React app with Nginx
5. ⏭️  **Monitoring:** Prometheus + Grafana
6. ⏭️  **CI/CD:** GitHub Actions pipeline

Ready to proceed with the Auth Service setup?

# FluxCD를 이용한 애플리케이션 배포 (GitOps)

이 문서는 `deployment/` 디렉토리에 정의된 Spring Boot 애플리케이션과 PostgreSQL 데이터베이스를 FluxCD를 사용하여 GitOps 방식으로 Kubernetes에 배포하는 방법을 안내합니다.

## GitOps와 FluxCD 소개

**GitOps**는 Git을 단일 진실 공급원(Single Source of Truth)으로 사용하여 인프라와 애플리케이션 배포를 관리하는 방식입니다. 모든 설정은 Git 리포지토리에 선언적으로 저장되며, 승인된 변경 사항이 Git에 푸시되면 클러스터 상태가 자동으로 업데이트됩니다.

**FluxCD**는 Kubernetes를 위한 GitOps 도구 세트입니다. 클러스터 내부에서 실행되며, Git 리포지토리의 상태와 클러스터의 상태를 지속적으로 동기화합니다.

이 가이드에서는 기존의 PostgreSQL 데이터베이스와 Spring Boot 애플리케이션 설정을 FluxCD가 관리하는 Git 리포지토리로 옮기는 과정을 설명합니다.

## 전제 조건

*   실행 중인 Kubernetes 클러스터 (예: `kind`, `minikube` 또는 클라우드 제공업체)
*   클러스터에 연결되도록 구성된 `kubectl`
*   사용할 Git 리포지토리 및 Personal Access Token (예: GitHub, GitLab)
*   [Flux CLI](https://fluxcd.io/flux/installation/) 설치

## 1단계: 구성 리포지토리 구조화

FluxCD는 Git 리포지토리의 특정 디렉토리 구조를 따릅니다. `deployment/`에 있는 모든 YAML 파일을 관리하기 위해 다음과 같은 구조를 권장합니다.

```
.
├── clusters/
│   └── my-cluster/          # 클러스터별 Flux 설정
│       ├── infrastructure.yaml
│       └── apps.yaml
└── infrastructure/
    └── database/            # 데이터베이스 관련 매니페스트
        ├── kustomization.yaml
        ├── db-configmap.yaml
        ├── db-deployment.yaml
        ├── db-pvc.yaml
        ├── db-secret.yaml
        └── db-service.yaml
└── apps/
    └── spring-boot-app/     # 애플리케이션 관련 매니페스트
        ├── kustomization.yaml
        ├── deployment.yaml
        └── service.yaml
```

*   `clusters/my-cluster/`: 특정 클러스터에 적용할 Flux `Kustomization` 리소스를 정의합니다.
*   `infrastructure/database/`: 데이터베이스와 같이 애플리케이션의 기반이 되는 인프라 구성요소를 그룹화합니다.
*   `apps/spring-boot-app/`: 비즈니스 로직을 담고 있는 애플리케이션을 그룹화합니다.

## 2단계: Flux 부트스트랩

먼저, Flux를 클러스터에 설치하고 Git 리포지토리와 동기화해야 합니다.

```bash
# 환경 변수 설정 (GitHub 예시)
export GITHUB_USER="<your-github-username>"
export GITHUB_TOKEN="<your-github-personal-access-token>"
export GITHUB_REPO="<your-config-repo-name>"

# Flux 부트스트랩 실행
flux bootstrap github \
  --owner=$GITHUB_USER \
  --repository=$GITHUB_REPO \
  --branch=main \
  --path=./clusters/my-cluster \
  --personal
```

이 명령어는 다음을 수행합니다.
1.  지정한 GitHub 리포지토리가 없으면 생성합니다.
2.  클러스터에 Flux 컴포넌트를 설치합니다.
3.  리포지토리에 배포 키를 등록하고, `--path`로 지정된 디렉토리를 동기화하도록 Flux를 설정합니다.

## 3단계: 매니페스트 및 Kustomization 파일 생성

이제 로컬에 복제된 Git 리포지토리에서 위에서 설계한 구조대로 파일을 정리하고 생성합니다.

#### 1. 데이터베이스 매니페스트 (`infrastructure/database/`)

`deployment/` 디렉토리에서 `db-*.yaml` 파일들을 `infrastructure/database/`로 복사합니다.

그리고 `infrastructure/database/kustomization.yaml` 파일을 생성하여 리소스들을 하나로 묶습니다.

**`infrastructure/database/kustomization.yaml`**
```yaml
apiversion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - db-configmap.yaml
  - db-deployment.yaml
  - db-pvc.yaml
  - db-secret.yaml
  - db-service.yaml
```

#### 2. 애플리케이션 매니페스트 (`apps/spring-boot-app/`)

`deployment/` 디렉토리에서 `deployment.yaml`과 `service.yaml`을 `apps/spring-boot-app/`으로 복사합니다.

그리고 `apps/spring-boot-app/kustomization.yaml` 파일을 생성합니다.

**`apps/spring-boot-app/kustomization.yaml`**
```yaml
apiversion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - deployment.yaml
  - service.yaml
```

## 4단계: Flux Kustomization 리소스 정의

이제 Flux가 어떤 디렉토리를 동기화해야 하는지 알려주는 `Kustomization` 리소스를 `clusters/my-cluster/` 디렉토리에 생성합니다.

#### 1. 인프라 (데이터베이스)
**`clusters/my-cluster/infrastructure.yaml`**
```yaml
apiversion: kustomize.toolkit.fluxcd.io/v1
kind: Kustomization
metadata:
  name: infrastructure
  namespace: flux-system
spec:
  interval: 10m0s
  path: ./infrastructure/database
  prune: true
  sourceRef:
    kind: GitRepository
    name: flux-system
```

#### 2. 애플리케이션
애플리케이션은 데이터베이스가 준비된 후에 배포되어야 합니다. `dependsOn`을 사용하여 배포 순서를 제어할 수 있습니다.

**`clusters/my-cluster/apps.yaml`**
```yaml
apiversion: kustomize.toolkit.fluxcd.io/v1
kind: Kustomization
metadata:
  name: apps
  namespace: flux-system
spec:
  interval: 10m0s
  path: ./apps/spring-boot-app
  prune: true
  sourceRef:
    kind: GitRepository
    name: flux-system
  dependsOn:
    - name: infrastructure
```

## 5단계: 변경 사항 커밋 및 푸시

생성하고 수정한 모든 파일을 Git 리포지토리에 커밋하고 푸시합니다.

```bash
git add .
git commit -m "Add database and spring boot app manifests for Flux"
git push
```

## 6단계: 배포 확인

Flux가 Git 리포지토리의 변경 사항을 감지하고 클러스터에 자동으로 적용합니다.

```bash
# Flux Kustomization 상태 확인
flux get kustomizations --watch

# 네임스페이스의 Pod 확인
kubectl get pods -n default

# 배포 상태 확인
kubectl get deployments -n default
```

이제 `postgres-deployment`와 `spring-boot-app` 배포가 성공적으로 실행되는 것을 확인할 수 있습니다. 앞으로는 Git 리포지토리의 매니페스트만 수정하면 Flux가 알아서 클러스터 상태를 업데이트합니다.

```
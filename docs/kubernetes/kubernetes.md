
# 쿠버네티스 주요 컴포넌트

## 요약

이 문서는 쿠버네티스의 핵심 컴포넌트에 대한 요약 정보를 제공합니다.

*   **Deployment:** 애플리케이션의 배포, 확장 및 롤백을 관리합니다.
*   **Service:** 파드 그룹에 접근하기 위한 안정적인 엔드포인트를 제공하고 로드 밸런싱을 지원합니다.
*   **ConfigMap:** 민감하지 않은 설정 데이터를 애플리케이션 코드에서 분리하여 저장합니다.
*   **PersistentVolumeClaim (PVC):** 애플리케이션이 기본 인프라를 몰라도 스토리지를 요청하고 사용할 수 있도록 합니다.
*   **Secret:** 암호 및 API 키와 같은 민감한 정보를 저장하고 관리합니다.


이 문서는 쿠버네티스의 주요 컴포넌트인 Deployment, Service, ConfigMap, PersistentVolumeClaim (PVC), Secret에 대해 설명합니다.

## Deployment (디플로이먼트)

디플로이먼트는 파드(Pod)와 레플리카셋(ReplicaSet)에 대한 선언적 업데이트를 제공하는 쿠버네티스 리소스 객체입니다. 디플로이먼트를 사용하면 애플리케이션의 배포, 확장, 롤백 등을 관리할 수 있습니다.

**주요 기능:**

*   **파드와 레플리카셋 관리:** 디플로이먼트는 레플리카셋을 생성하고 관리하며, 레플리카셋은 지정된 수의 파드 복제본이 항상 실행되도록 보장합니다.
*   **선언적 업데이트:** YAML 파일을 통해 원하는 상태를 정의하면 쿠버네티스가 해당 상태를 달성하기 위해 작동합니다.
*   **롤링 업데이트:** 다운타임 없이 애플리케이션의 새 버전을 점진적으로 배포할 수 있습니다.
*   **롤백:** 업데이트에 문제가 발생하면 이전의 안정적인 버전으로 쉽게 롤백할 수 있습니다.
*   **확장:** 디플로이먼트 설정을 변경하여 애플리케이션을 쉽게 확장하거나 축소할 수 있습니다.

**예시 (deployment.yaml):**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app
    spec:
      containers:
      - name: nginx
        image: nginx:latest
        ports:
        - containerPort: 80
```

## Service (서비스)

서비스는 파드 집합에 접근하기 위한 안정적인 엔드포인트를 제공하는 추상화된 방법입니다. 파드는 일시적이므로 IP 주소가 변경될 수 있지만, 서비스는 고유한 DNS 이름을 가지며 파드 집합에 대한 로드 밸런싱을 제공합니다.

**주요 기능:**

*   **안정적인 엔드포인트:** 서비스는 고유한 IP 주소와 DNS 이름을 가지므로, 파드의 IP가 변경되어도 서비스에 안정적으로 접근할 수 있습니다.
*   **로드 밸런싱:** 서비스는 여러 파드에 걸쳐 네트워크 트래픽을 분산하여 부하를 분산합니다.
*   **서비스 검색:** 클러스터 내에서 다른 애플리케이션을 쉽게 찾을 수 있도록 도와줍니다.

**서비스 종류:**

*   **ClusterIP:** 클러스터 내부에서만 접근할 수 있는 가상 IP를 생성합니다. (기본값)
*   **NodePort:** 각 노드의 IP 주소와 정적 포트를 통해 외부에서 서비스에 접근할 수 있도록 합니다.
*   **LoadBalancer:** 클라우드 공급자의 로드 밸런서를 사용하여 서비스를 외부에 노출합니다.
*   **ExternalName:** CNAME 레코드를 반환하여 서비스를 외부 서비스에 매핑합니다.

**예시 (service.yaml):**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  selector:
    app: MyApp
  ports:
    - protocol: TCP
      port: 80
      targetPort: 9376
  type: LoadBalancer
```

## ConfigMap (컨피그맵)

컨피그맵은 민감하지 않은 설정 데이터를 키-값 쌍으로 저장하는 데 사용되는 API 객체입니다. 컨피그맵을 사용하면 애플리케이션 코드에서 설정을 분리하여 이식성을 높일 수 있습니다.

**주요 특징:**

*   **설정 분리:** 컨테이너 이미지에서 설정을 분리하여 애플리케이션을 더 쉽게 관리할 수 있습니다.
*   **환경 일관성:** 동일한 컨테이너 이미지를 사용하여 개발, 스테이징, 프로덕션 등 다양한 환경에 대한 서로 다른 설정을 유지할 수 있습니다.

**사용 방법:**

*   **환경 변수:** 컨피그맵의 데이터를 컨테이너의 환경 변수로 주입할 수 있습니다.
*   **볼륨 마운트:** 컨피그맵을 볼륨으로 마운트하여 파일로 사용할 수 있습니다.

**예시 (configmap.yaml):**

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: my-config
data:
  database.host: "mysql"
  log_level: "info"
```

## PersistentVolumeClaim (PVC)

PVC는 사용자가 스토리지(저장 공간)를 요청하는 방법입니다. PVC를 사용하면 애플리케이션이 기본 스토리지 인프라의 세부 정보를 알 필요 없이 스토리지 리소스를 요청하고 사용할 수 있습니다.

**주요 개념:**

*   **PersistentVolume (PV):** 관리자가 프로비저닝했거나 스토리지 클래스에 의해 동적으로 프로비저닝된 클러스터의 스토리지 조각입니다.
*   **StorageClass:** 관리자가 제공하는 다양한 "클래스"의 스토리지를 설명하는 방법을 제공합니다.

**워크플로우:**

1.  **스토리지 요청 (PVC):** 사용자가 필요한 스토리지 양과 접근 모드를 지정하여 PVC를 생성합니다.
2.  **프로비저닝 (정적 또는 동적):**
    *   **정적 프로비저닝:** 관리자가 미리 여러 PV를 생성합니다.
    *   **동적 프로비저닝:** 적합한 정적 PV가 없는 경우 스토리지 클래스를 사용하여 PVC의 요구 사항과 일치하는 새 PV를 자동으로 프로비저닝합니다.
3.  **바인딩:** 쿠버네티스는 PVC를 적합한 PV에 바인딩합니다.
4.  **사용:** 파드는 PVC를 볼륨으로 마운트하여 데이터를 읽고 쓸 수 있습니다.
5.  **회수:** 사용자가 볼륨 사용을 마치면 PVC를 삭제할 수 있습니다. PV의 회수 정책에 따라 기본 스토리지의 처리 방법이 결정됩니다.

**예시 (pvc.yaml):**

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: my-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 5Gi
  storageClassName: manual
```

## Secret (시크릿)

시크릿은 암호, OAuth 토큰, SSH 키와 같은 민감한 정보를 저장하고 관리하는 데 사용되는 객체입니다. 시크릿을 사용하면 민감한 데이터를 애플리케이션 코드나 컨테이너 이미지에 포함할 필요가 없습니다.

**주요 특징:**

*   **민감한 정보 저장:** 암호, API 키, 토큰 등과 같은 민감한 데이터를 저장합니다.
*   **보안:** 데이터는 Base64로 인코딩되어 저장되며, etcd에서 미사용 시 암호화를 활성화하여 보안을 강화할 수 있습니다.

**사용 방법:**

*   **환경 변수:** 시크릿의 값을 컨테이너의 환경 변수로 주입할 수 있습니다.
*   **볼륨 마운트:** 시크릿을 볼륨으로 마운트하여 파일로 사용할 수 있습니다.
*   **이미지 풀 시크릿:** 프라이빗 컨테이너 레지스트리에서 이미지를 가져오는 데 사용할 수 있습니다.

**예시 (secret.yaml):**

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: my-secret
type: Opaque
data:
  username: YWRtaW4=
  password: cGFzc3dvcmQ=
```

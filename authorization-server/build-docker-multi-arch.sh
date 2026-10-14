#! /bin/bash -e

SCRIPT_DIR=$(cd "$( dirname "${BASH_SOURCE[0]}" )" ; pwd)

BUILD_NATIVE=

while [ $# -gt 0 ]; do
  case "$1" in
    --native)
      BUILD_NATIVE=yes
      echo BUILD_NATIVE=yes
      shift
      ;;
    *)
      echo "Usage: $0 [--native]"
      exit 1
  esac
done

"${SCRIPT_DIR}"/../gradlew :authorization-server:assemble

SERVICE_IMAGE_VERSION=$(sed -e '/^version=/!d' -e 's/version=//' < "${SCRIPT_DIR}/../gradle.properties")

BUILDX_ARGS=()

if [ -n "$BUILD_NATIVE" ] ; then
  "${SCRIPT_DIR}"/../gradlew :authorization-server:prepareForNativeBuild
  BUILDX_ARGS=(-f Dockerfile-native)
fi

if [ -z "$AUTHORIZATION_SERVER_MULTI_ARCH_IMAGE" ] ; then
  docker-compose -f "$SCRIPT_DIR"/../docker-compose-registry.yml --project-name eventuate-common-registry up -d registry
fi

docker buildx build "${BUILDX_ARGS[@]}" --build-arg serviceImageVersion="${SERVICE_IMAGE_VERSION}" --platform "${BUILDX_TARGET_PLATFORMS:-linux/amd64,linux/arm64}" \
  -t "${AUTHORIZATION_SERVER_MULTI_ARCH_IMAGE:-${DOCKER_HOST_NAME:-host.docker.internal}:5002/eventuate-examples-spring-authorization-server:multi-arch-local-build}" \
  "${BUILDX_PUSH_OPTIONS:---output=type=image,push=true,registry.insecure=true}" \
  "$SCRIPT_DIR"


#! /bin/bash -e

SCRIPT_DIR=$(cd $( dirname "${BASH_SOURCE[0]}" ) ; pwd)

if [ -z "$AUTHORIZATION_SERVER_MULTI_ARCH_IMAGE" ] ; then
  docker-compose -f $SCRIPT_DIR/../docker-compose-registry.yml --project-name eventuate-common-registry up -d registry
fi

docker buildx build --platform ${BUILDX_TARGET_PLATFORMS:-linux/amd64,linux/arm64} \
  -t ${AUTHORIZATION_SERVER_MULTI_ARCH_IMAGE:-${DOCKER_HOST_NAME:-host.docker.internal}:5002/eventuate-examples-spring-authorization-server:multi-arch-local-build} \
  ${BUILDX_PUSH_OPTIONS:---output=type=image,push=true,registry.insecure=true} \
  $SCRIPT_DIR


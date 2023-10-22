

export MULTI_ARCH_TAG=test-build-${CIRCLE_SHA1?}
export AUTHORIZATION_SERVER_MULTI_ARCH_IMAGE=eventuateio/eventuate-examples-spring-authorization-server:$MULTI_ARCH_TAG

export BUILDX_PUSH_OPTIONS=--push

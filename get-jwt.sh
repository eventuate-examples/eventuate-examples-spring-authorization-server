#! /bin/bash -e

args=

while [ -n "$1" ] ; do
  case $1 in
    -h|--help)
      echo "Usage: $0 [port]"
      exit 0
      ;;
    -*)
      args="$args $1"
      ;;
    *)
      break
      ;;
  esac
  shift
done

curl "$args" --fail -s -X POST -u messaging-client:secret -d "client_id=messaging-client" -d "username=user" -d "password=password" \
    -d "grant_type=password" \
    http://localhost:${1:-9000}/oauth2/token | jq -r .access_token
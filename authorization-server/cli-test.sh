#! /bin/bash -e

# curl localhost:9000/.well-known/openid-configuration | jq .

curl -X POST -u messaging-client:secret -d "grant_type=client_credentials" http://localhost:9000/oauth2/token | jq -r .access_token

curl -X POST -u messaging-client:secret -d "client_id=messaging-client" -d "username=user" -d "password=password" -d "grant_type=password" http://localhost:9000/oauth2/token | jq -r .access_token | jwt decode -


http -a messaging-client:secret --form POST http://localhost:9000/oauth2/token  grant_type=client_credentials | jq -r .access_token

http -a messaging-client:secret --form POST http://localhost:9000/oauth2/token  client_id=messaging-client username=user  password=password grant_type=password

curl --fail localhost:9000/.well-known/oauth-authorization-server | jq .

curl --fail http://localhost:9000/oauth2/jwks | jq .

http -a messaging-client:secret --form POST http://localhost:9000/oauth2/token  grant_type=client_credentials | jq -r .access_token | jwt decode -

# See https://github.com/mike-engel/jwt-cli




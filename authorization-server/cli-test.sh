#! /bin/bash -e

# curl localhost:9000/.well-known/openid-configuration | jq .


http -a messaging-client:secret --form POST http://localhost:9000/oauth2/token  grant_type=client_credentials | jq -r .access_token

http -a messaging-client:secret --form POST http://localhost:9000/oauth2/token  client_id=messaging-client username=user  password=password grant_type=password

curl localhost:9000/.well-known/openid-configuration | jq .

http -a messaging-client:secret --form POST http://localhost:9000/oauth2/token  grant_type=client_credentials | jq -r .access_token | jwt decode -

# See https://github.com/mike-engel/jwt-cli




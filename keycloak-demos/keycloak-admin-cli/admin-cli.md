
# Keycloak Admin CLI

See: http://blog.keycloak.org/2017/01/administer-keycloak-server-from-shell.html

## Enter Keycloak docker container
```
docker exec -it idmsystem_idm-keycloak_1 /bin/bash
```

## cd into Keycloak Directory
```
cd keycloak
```

## Configure Keycloak credentials
```
bin/kcadm.sh config credentials \
#  --server http://localhost:8080/auth \
  --server http://172.20.0.7:8080/u/auth \
  --realm master \
  --user admin
```

## Create new user
```
KC_USER_ID=$(\
  bin/kcadm.sh create users \
  -r javaland \
  -s username=cli \
  -i)

echo $KC_USER_ID
```

## List last events
```
bin/kcadm.sh get events --offset 0 --limit 100
```
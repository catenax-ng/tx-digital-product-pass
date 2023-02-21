<!--
 Copyright 2023 BASF SE, BMW AG, Henkel AG & Co. KGaA
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
     http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

# Local Keycloak Setup

This guide describes how to setup a keycloak instance in local docker container and import existing realm.json file.

## Launch keycloak docker container

- Keycloak official image: [jboss/keycloak](https://registry.hub.docker.com/r/jboss/keycloak)

```
docker run --name keycloak -p 8088:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin -d jboss/keycloak
```

## Import realm

Import the [realm.json](./realm.json) located in current directory

## Create users

After importing the realm, the users need to be created manually. 

***Example users:***
- **User 1:** company 1 user  \
  **Password:** changeme \
  **Role:** OEM, Dismantler

- **User 2:** company 2 user \
  **Password:** changeme \
  **Role:** Recycler

## Integration with vuejs app

### Enable keycloak configuration

Install the keycloak plugin for vuejs app from ```npm install keycloak-js```

The keycloak configurations are defined in [src/services/service.const.js](../../../src/services/service.const.js) and [src/services/Authentication.js](../../../src/services/Authentication.js) files for different deployment environments.

### Build and run the app

```
npm install --legacy-peer-deps
npm run serve
```
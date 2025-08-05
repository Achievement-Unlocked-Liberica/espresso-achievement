
# Environment Setup

## Java

Install Java JDK v 21 or Later

https://www.oracle.com/java/technologies/downloads/#java21

Create JAVA_HOME ENvironmment Variable and set it to the java iunstallation path

For example: JAVA_HOME = C:\Program Files\Java\jdk-21


## Maven

*Not Needed it seems that the Java Spring Extension would already have it* (⌐ ͡■ ͜ʖ ͡■)

## Spring

*Included in VSCode Extension* (⌐ ͡■ ͜ʖ ͡■)

## Docker

Install docker Desktop Personal Edition

https://www.docker.com/products/docker-desktop/

## VS Code Extensions

Spring Boot Extension Pack
https://marketplace.visualstudio.com/items?itemName=vmware.vscode-boot-dev-pack

Better Comments
https://marketplace.visualstudio.com/items?itemName=aaron-bond.better-comments

Extension Pack For Java
https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack

Docker
https://marketplace.visualstudio.com/items?itemName=ms-azuretools.vscode-docker
https://marketplace.visualstudio.com/items?itemName=formulahendry.docker-explorer

Kubernetes
https://marketplace.visualstudio.com/items?itemName=ms-kubernetes-tools.vscode-kubernetes-tools

Git Lens
https://marketplace.visualstudio.com/items?itemName=eamodio.gitlens

REST Client
https://marketplace.visualstudio.com/items?itemName=humao.rest-client

VS Code Icons
https://marketplace.visualstudio.com/items?itemName=vscode-icons-team.vscode-icons

VS Code Pets  
https://marketplace.visualstudio.com/items?itemName=tonybaloney.vscode-pets

ASCII Emojis   (⌐ ͡■ ͜ʖ ͡■)
https://asciimoji.com/

# Building & Running

## Navigate to the project directory
```bash
cd /path/to/your/project
```

## Build the project with Maven with Tests
```bash
mvnw clean install
```

## Build the project with Maven without Tests
```bash
mvnw clean install -DskipTests
```

## Run the project
```bash
mvnw spring-boot:run
```

## Open Swagger API page

http://localhost:8080/swagger-ui/index.html

Port number might be different.  Check your application.properties file


## Run REST Client Calls

Evert service in each moduile has a filename called *restClient.http*

Execute each request one by one to test the API


# Building & Packaging

## Navigate to the project directory
```bash
cd /path/to/your/project
```

## Docker Build & Run (Manually)

```bash
docker compose up -d --build
```

## Docker GitHub Login & Push

<!-- ???? -->
export CR_PAT=YOUR_TOKEN
echo $CR_PAT | docker login ghcr.io -u USERNAME --password-stdin

## Helm Install, Create and Package
```
helm create infrastructure/espresso-service 
```

```
helm package infrastructure/espresso-service -d infrastructure/packages
```

```
//REVIEW THIS LINE
helm install espresso-service infrastructure/packages/espresso-service-0.1.0.tgz --set image.tag=1.1.1 -n liberica-services --create-namespace
```


## Helpful Links

*Validating Objects (Entities) in Spring Boot*
https://medium.com/@bouguern.mohamed/validating-objects-entities-in-spring-boot-9757fc01211f

*Best Postman Alternatives for API Testing in 2025*
https://katalon.com/resources-center/blog/postman-alternatives-api-testing

*Bruno is a Git-integrated, fully offline, and open-source API client*
https://www.usebruno.com/

*REST Client for VSCode*
https://github.com/Huachao/vscode-restclient

*Managing GitHub Access Tokens*
https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens
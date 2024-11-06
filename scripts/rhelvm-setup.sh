#!/bin/bash

sdk install java 21.0.5-tem

cd /home/instruct/parasol-insurance/app/
./mvnw clean package -DskipTests

nohup java -jar -Dquarkus.langchain4j.openai.parasol-chat.base-url=http://localhost:8000/v1 /home/instruct/parasol-insurance/app/target/quarkus-app/quarkus-run.jar > ~/quarkus.out 2>&1 &
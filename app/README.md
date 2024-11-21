By default the app will assume there is a chat model on https://localhost:8000/v1 that exposes an OpenAI endpoint.

# Using Ollama

If you would like to use [Ollama](https://ollama.com/) instead, first install/run Ollama on your machine. Then do one of the following:

## Building the app
When building the app, run `./mvnw clean package -DskipTests -Dquarkus.profile=ollama,prod` (or `quarkus build --clean --no-tests -Dquarkus.profile=ollama,prod`)

## Running dev mode
When running dev mode, run `./mvnw quarkus:dev -Dquarkus.profile=ollama` (or `quarkus dev -Dquarkus.profile=ollama`).

## Running tests
When running tests, run `./mvnw verify -Dquarkus.test.profile=ollama,test -Dquarkus.profile=ollama` (or `quarkus build --tests -Dquarkus.test.profile=ollama,test -Dquarkus.profile=ollama`)

## Running the app outside dev mode
If you want to run the app outside dev mode, first build the app as described above, then run `java -Dquarkus.profile=ollama,prod -jar target/quarkus-app/quarkus-run.jar`

# Using Jlama

[Jlama](https://github.com/tjake/Jlama) is a pure Java inference engine. Using it the LLM inference will be executed directly embedded in the same JVM running the Quarkus application.

## Building the app
When building the app, run `./mvnw clean package -DskipTests -Dquarkus.profile=jlama,prod` (or `quarkus build --clean --no-tests -Dquarkus.profile=jlama,prod`)

## Running dev mode
When running in dev mode, Quarkus explicitly disables C2 compilation making Jlama extremely slow to the point of being unusable. This issue will be fixed in Quarkus 3.17, but for now it is strongly suggested to avoid using Jlama in dev mode.

## Running the app
To run the app outside dev mode first run the app as described above, then run `java --enable-preview --enable-native-access=ALL-UNNAMED --add-modules jdk.incubator.vector -Dquarkus.profile=jlama,prod -jar target/quarkus-app/quarkus-run.jar`. This command launching the JVM enabling the Vector API that are required by Jlama, but still only a preview feature.
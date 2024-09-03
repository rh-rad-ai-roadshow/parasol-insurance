# Parasol Insurance

A [Quarkus](https://quarkus.io) + [React](https://react.dev/) AI app for managing fictitious insurance claims. Uses [Quarkus Quinoa](https://docs.quarkiverse.io/quarkus-quinoa/dev/index.html) under the covers.

![App](webui/src/app/assets/images/sample.png)

## Pre-requisites

- Java 21 or later -- Get it https://adoptium.net/  or install using your favorite package manager.
- Maven 3.9.6 or later -- Get it https://maven.apache.org/download.cgi or install using your favorite package manager.
    - Or just use the embedded [Maven Wrapper](https://maven.apache.org/wrapper)
- An OpenAI-capable LLM inference server. Get one here with [InstructLab](https://github.com/instructlab/instructlab)!


<details>
    <summary>Node.js Pre-requisites</summary>

- Node.js 18 or later -- Get it https://nodejs.org/en/download .
- npm 10+ -- Node.js includes npm
- An OpenAI-capable LLM inference server. Get one here with [InstructLab](https://github.com/instructlab/instructlab)!
</details>

## To Configure on InstructLab instance of Red Hat Demo Platform

You can execute [this]([https://gist.githubusercontent.com/jameslabocki/748e191006d0e311dec21c72e95570d1/raw/3c40273c962c3ee598a13dd853b831ac9df884ff/gistfile1.txt](https://raw.githubusercontent.com/jameslabocki/ilabdemo/main/install.sh)) to install the parasol app on the InstructLab instance for Red Hat Demo Platform.

## Configuration

You can change the coordinates (host/port and other stuff) for the LLM and backend in [`app/src/main/resources/application.properties`](app/src/main/resources/application.properties).

<details>
<summary>For Node.js:</summary>

You can change the coordinates (host/port and other stuff) for the LLM and backend in [`node-app/.env`](node-app/.env)

</details>

## Running

First, get your inference server up and running. For example, with [InstructLab](https://github.com/instructlab/instructlab), the default after running `ilab serve` is that the server is listening on `localhost:8000`. This is the default for this app as well.

Then:

```
cd app;
./mvnw clean quarkus:dev
```

<details>
<summary>For Node.js:</summary>

First Build the webui:

```
cd node-app
npm run buildui
```

Then run the node.js app:

```
npm install
node server.mjs
```
</details>

App will open on `http://0.0.0.0:8005`.

Open the app, click on a claim, click on the chat app, and start asking questions. The context of the claim is sent to the LLM along with your Query, and the response is shown in the chat (it may take time depending on your machine's performance).

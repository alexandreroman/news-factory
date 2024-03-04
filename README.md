# News Factory: create AI-generated newsletters

This project relies on [OpenAI](https://openai.com/) to generate a newsletter.
Enter your favorite topics, add your own links, and let the app generates a digest
for you. Behind the scenes, webpages are downloaded, and the content is summarized
using the LLM. The app takes care of rendering the result in a single page.

![Screenshot of the application](app.png)

This app is powered by [Spring AI](https://spring.io/projects/spring-ai),
an open-source Spring project which simplifies the use of AI technologies for your
Java project.

## How to build and run this app?

Use Java 17+ to build and run this app. 

Use this command to build the app:

```shell
./mvnw clean package
```

## AI Providers

The application was tested with 3 AI providers: OpenAI, Azure OpenAI and AWS Bedrock based on Llama2. Choose the one you preferred and enable it based on the options below.

### OpenAI

You need an OpenAI API key to run this app.
[Refer to this page](https://help.openai.com/en/articles/4936850-where-do-i-find-my-openai-api-key)
to get an API key.

Set your API key as an environment variable:

```shell
export OPENAI_API_KEY=xxxxxx
```
Set `newsletter.ai.model` to `openai` in `application.properties`.

Only uncomment `spring-ai-openai-spring-boot-starter` in `pom.xml`, comment the other two spring-ai dependencies.

### AWS Bedrock based on Llama2
It appears that currently this model is only available at us-east-1.
Enable the API at https://us-east-1.console.aws.amazon.com/bedrock/home?region=us-east-1#/

Then export your AWS key and secret:

```shell
export AWS_CLIENT_ID=xxxxxxxxxx
export AWS_SECRET=xxxxxxxxx
```

Change the propety `spring.ai.bedrock.llama2.chat.enabled` to `true` in `application.properties`.

Set `newsletter.ai.model` to `bedrock-llama2` in `application.properties`.

Only uncomment `spring-ai-bedrock-ai-spring-boot-starter` in `pom.xml`, comment the other two spring-ai dependencies.

### Azure OpenAI
Enable the Azure OpenAI service in the Azure portal. This requires to fill out a form at the moment, which usually takes at most 24 hours.
Create the service at https://portal.azure.com/#create/Microsoft.CognitiveServicesOpenAI.
Create an Azure OpenAI deployment at https://oai.azure.com/portal.

Export the following environment variables:

```shell
export AZURE_OPENAPI_KEY=xxxxxxx
export AZURE_OPENAPI_ENDPOINT=https://xxxxxx.openai.azure.com/
export AZURE_OPENAPI_DEPLOYMENT=xxxxxxx
```
Set `newsletter.ai.model` to `azure-openai` in `application.properties`.

Only uncomment `spring-ai-azure-openai-spring-boot-starter` in `pom.xml`, comment the other two spring-ai dependencies.

Use this command to run the app on your local workstation:

```shell
./mvnw spring-boot:run
```

The app is available at http://localhost:8080.

## Contribute

Contributions are always welcome!

Feel free to open issues & send PR.

## License

Copyright &copy; 2024 [Broadcom, Inc. or its affiliates](https://vmware.com).

This project is licensed under the [Apache Software License version 2.0](https://www.apache.org/licenses/LICENSE-2.0).

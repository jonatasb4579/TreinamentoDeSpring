package br.com.alura.screenmatch.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

public class ConsultaChatGPT {
    public static String obterTraducao(String texto) {

        
        OpenAiService service = new OpenAiService
                ("sk-proj-vu7otYkk2YOML9XZBoROCKhZHh_9oL4qi08nTYXCz_t8Ak" +
                        "4h623pDdbjsUWCGau3Lgj8mJERjQT3BlbkFJq-9lHuogqfiRTdZEXR9CWd0DzLkr81QjlmJbKIrPTloHf8ZArRDfvQFfYHV6LPtwWKWjmf5qEA");


        CompletionRequest requisicao = CompletionRequest.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt("traduza para o português o texto: " + texto)
                .maxTokens(1000)
                .temperature(0.7)
                .build();


        var resposta = service.createCompletion(requisicao);
        return resposta.getChoices().get(0).getText();
    }
}

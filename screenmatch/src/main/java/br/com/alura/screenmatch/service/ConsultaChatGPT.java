package br.com.alura.screenmatch.service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;


import java.util.Optional;

public class ConsultaChatGPT {

    public static Optional<String> obterTraducao(String texto) {

        OpenAIClient client = OpenAIOkHttpClient.fromEnv();

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_3_5_TURBO)                // ou ChatModel.GPT_4_1
                .addUserMessage("Traduza para o português o texto: " + texto)
                .maxTokens(1000)
                .temperature(0.7)
                .build();

        ChatCompletion chatCompletion = client
                .chat()
                .completions()
                .create(params);

        return chatCompletion
                .choices()      // em vez de getChoices()
                .get(0)
                .message()      // em vez de getMessage()
                .content();
    }
}
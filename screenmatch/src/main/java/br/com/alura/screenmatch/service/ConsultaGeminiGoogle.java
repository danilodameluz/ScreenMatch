package br.com.alura.screenmatch.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class ConsultaGeminiGoogle {
    public static String obterTraducao(String texto) {
        Client client = new Client();

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        "Traduza para o português o texto: " + texto,
                        null);

        return response.text();
    }
}

package com.example.saldoplusv1;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenAIHelper {

    public interface ResponseCallback {
        void onSuccess(String jsonResponse);
        void onError(String errorMessage);
    }

    public static void enviarImagen(Bitmap bitmap, String apiKey, ResponseCallback callback) {
        OkHttpClient client = new OkHttpClient();

        // Convertir la imagen a base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageBase64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        // Crear el JSON para la API
        String json = "{\n" +
                "  \"model\": \"gpt-4o\",\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": [\n" +
                "        {\n" +
                "          \"type\": \"image_url\",\n" +
                "          \"image_url\": {\n" +
                "            \"url\": \"data:image/jpeg;base64," + imageBase64 + "\"\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"text\",\n" +
                "          \"text\": \"Solo respóndeme en JSON, sin texto adicional. Analiza este ticket e identifica productos y precios, y  el total. Devuelve SOLO un JSON como este ejemplo [{\\\"producto\\\":\\\"Pan\\\", \\\"precio\\\":28.50}]\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        // Hacer la petición
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error de red: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Error en la respuesta: " + response.message());
                    return;
                }

                String result = response.body().string();
                callback.onSuccess(result);
            }
        });
    }
}

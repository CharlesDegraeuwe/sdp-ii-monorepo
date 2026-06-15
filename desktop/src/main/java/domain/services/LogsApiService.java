package domain.services;

import domain.dto.LogDTO;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class LogsApiService extends ApiService {

    Dotenv dotenv = Dotenv.load();
    private final String BASE_URL = dotenv.get("BASE_URL") +"/logs";

    //calls etc
    // ====================================================================
    public List<LogDTO> getAlleLogs() {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return List.of();
            LogDTO[] array = mapper.readValue(response.body(), LogDTO[].class);
            return Arrays.asList(array);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen logs", e);
        }
    }


    //activatie van user zelf met code
    // ====================================================================
    public String voegLogToe(LogDTO log) {
        try {
            String jsonBody = mapper.writeValueAsString(log);
            HttpRequest request = authenticatedRequest(BASE_URL)
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            return body != null ? body : "";
        } catch (Exception e) {
            throw new RuntimeException("Fout bij toevoegen van log", e);
        }
    }

    //zoeken op id
    // ====================================================================
    public LogDTO zoekOpId(int id) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/log?id=" + id)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return null;
            String body = response.body();
            if (body == null || body.isBlank()) return null;
            return mapper.readValue(body, LogDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen log", e);
        }
    }
}

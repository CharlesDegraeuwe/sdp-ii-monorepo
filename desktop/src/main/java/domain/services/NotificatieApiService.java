package domain.services;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.dto.NotificatieDTO;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class NotificatieApiService extends ApiService {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private final String BASE_URL = dotenv.get("BASE_URL") + "/notificaties";

    public List<NotificatieDTO> geefNotificaties(int werknemerId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + werknemerId).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return List.of();
            String body = response.body();
            if (body == null || body.isBlank()) return List.of();
            return mapper.readValue(body, new TypeReference<List<NotificatieDTO>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    public long geefAantalOngelezen(int werknemerId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + werknemerId + "/ongelezen").GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            if (body == null || body.isBlank()) return 0;
            return Long.parseLong(body.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public String markeerAlsGelezen(int notificatieId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + notificatieId + "/gelezen")
                    .PUT(HttpRequest.BodyPublishers.noBody()).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            return body != null ? body : "";
        } catch (Exception e) {
            return "";
        }
    }

    public String verwijderNotificatie(int notificatieId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + notificatieId)
                    .DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            return body != null ? body : "";
        } catch (Exception e) {
            return "";
        }
    }
}

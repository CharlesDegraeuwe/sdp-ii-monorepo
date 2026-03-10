package domain.facades;

import domain.dto.UpdateWerknemerDTO;
import domain.dto.WerknemerDTO;
import domain.services.WerknemersApiService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class WerknemersFacade {
    private final WerknemersApiService api = new WerknemersApiService();

    public List<WerknemerDTO> geefAlleWerknemers() {
        return api.getAlleWerknemers();
    }

    public void activeerWerknemer(String code) {
       api.activeerWerknemer(code);
    }

    public void deactiveerWerknemer(int werknemerId) {

    }

    public WerknemerDTO zoekOpEmail(String email) {
        return api.zoekOpEmail(email);
    }

    public WerknemerDTO zoekOpId(int id) {
        return api.zoekOpId(id);
    }


    public void update(UpdateWerknemerDTO werknemer) {
        api.update(werknemer);

    }

    public boolean veranderStatus(int werknemerId, String actie) {
        try {
            String url = "http://localhost:8080/api/werknemers/" + werknemerId + "/" + actie;

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (Exception e) {
            System.err.println("Fout bij het aanroepen van API: " + e.getMessage());
            return false;
        }
    }
}
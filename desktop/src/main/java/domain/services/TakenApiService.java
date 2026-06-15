package domain.services;

import domain.dto.TaakDTO;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TakenApiService extends ApiService {
    private final String BASE_URL = Dotenv.load().get("BASE_URL") + "/taken";

    public List<TaakDTO> geefTakenVanWerknemer(int werknemerId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/werknemer/" + werknemerId)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            TaakDTO[] array = mapper.readValue(response.body(), TaakDTO[].class);
            return Arrays.asList(array);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen taken", e);
        }
    }

    public List<TaakDTO> geefAlleTaken() {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/alle").GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            TaakDTO[] array = mapper.readValue(response.body(), TaakDTO[].class);
            return Arrays.asList(array);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen alle taken", e);
        }
    }

    public String wijsTaakToe(int taakId, int werknemerId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + taakId + "/toewijzen?werknemerId=" + werknemerId)
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Fout bij toewijzen taak", e);
        }
    }

    public String maakTaakAan(int werknemerId, String titel, String beschrijving, LocalDate deadline, int siteId) {
        try {
            String json = mapper.writeValueAsString(new TaakAanmakenRequest(werknemerId, titel, beschrijving, deadline, siteId));
            HttpRequest request = authenticatedRequest(BASE_URL)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Fout bij aanmaken taak", e);
        }
    }

    private record TaakAanmakenRequest(int werknemerId, String titel, String beschrijving, java.time.LocalDate deadline, int siteId) {}

    public String wijzigTaak(int taakId, String titel, String beschrijving) {
        try {
            String json = mapper.writeValueAsString(new TaakWijzigenRequest(titel, beschrijving));
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + taakId)
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Fout bij wijzigen taak", e);
        }
    }

    private record TaakWijzigenRequest(String titel, String beschrijving) {}

    public String verwijderTaak(int taakId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + taakId)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Fout bij verwijderen taak", e);
        }
    }

    public String markeerAfgewerkt(int taakId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + taakId + "/afgewerkt")
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Fout bij markeren als afgewerkt", e);
        }
    }
}
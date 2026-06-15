package domain.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import domain.dto.UpdateWerknemerDTO;
import domain.dto.WerknemerDTO;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class WerknemersApiService {
    Dotenv dotenv = Dotenv.load();
    private final String BASE_URL = dotenv.get("BASE_URL") +"/werknemers";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    //calls etc
    // ====================================================================
    public List<WerknemerDTO> getAlleWerknemers() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/users"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            WerknemerDTO[] array = mapper.readValue(response.body(), WerknemerDTO[].class);
            return Arrays.asList(array);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen werknemers", e);
        }
    }


    //activatie van user zelf met code
    // ====================================================================
    public String activeerWerknemer(String code) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/activeer?code=" + code))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Fout bij activeren van werknemer", e);
        }
    }


    //activatie van user zelf met code
    // ====================================================================
    public String toggleWerkNemerActivatie() {
        return null;
    }


    //zoeke op email en ww
    // ====================================================================
    public WerknemerDTO zoekOpEmail(String email) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/user?email=" + email))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), WerknemerDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen werknemer", e);
        }
    }


    //updaten
    // ====================================================================
    public WerknemerDTO update(UpdateWerknemerDTO dto) {
        try {
            String json = mapper.writeValueAsString(dto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/update"))
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), WerknemerDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij updaten werknemer", e);
        }
    }

    //zoeken op id
    // ====================================================================
    public WerknemerDTO zoekOpId(int id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/user?id=" + id))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), WerknemerDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen werknemer", e);
        }
    }

    public boolean veranderStatus(int werknemerId, String actie) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + werknemerId + "/" + actie))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (Exception e) {
            throw new RuntimeException("Fout bij status wijzigen", e);
        }
    }

    public boolean registreerWerknemer(String naam, String voornaam, String email, String telefoon, String geboortedatum, String rol) {
        try {
            String jsonBody = String.format(
                    "{\"naam\":\"%s\",\"voornaam\":\"%s\",\"email\":\"%s\",\"wachtwoord\":\"Wachtwoord123\",\"telefoonnummer\":\"%s\",\"geboortedatum\":\"%s\",\"rol\":\"%s\"}",
                    naam, voornaam, email, telefoon, geboortedatum, rol
            );
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (Exception e) {
            throw new RuntimeException("Fout bij registreren werknemer", e);
        }
    }
}

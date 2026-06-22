package domain.services;

import domain.dto.UpdateWerknemerDTO;
import domain.dto.WerknemerDTO;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class WerknemersApiService extends ApiService {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private final String BASE_URL = dotenv.get("BASE_URL") +"/werknemers";

    //calls etc
    // ====================================================================
    public List<WerknemerDTO> getAlleWerknemers() {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL)
                .GET()
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return List.of();
            String body = response.body();
            if (body == null || body.isBlank()) return List.of();
            WerknemerDTO[] array = mapper.readValue(body, WerknemerDTO[].class);
            return Arrays.asList(array);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen werknemers", e);
        }
    }


    //activatie van user zelf met code
    // ====================================================================
    public String activeerWerknemer(String code) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/activeer?code=" + code)
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
            HttpRequest request = authenticatedRequest(BASE_URL + "/email/" + email)
                .GET()
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return null;
            String body = response.body();
            if (body == null || body.isBlank()) return null;
            return mapper.readValue(body, WerknemerDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen werknemer", e);
        }
    }


    //updaten
    // ====================================================================
    public WerknemerDTO update(UpdateWerknemerDTO dto) {
        try {
            String json = mapper.writeValueAsString(dto);

            HttpRequest request = authenticatedRequest(BASE_URL + "/" + dto.id())
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300)
                throw new RuntimeException("Server fout " + response.statusCode());
            String body = response.body();
            if (body == null || body.isBlank()) return null;
            return mapper.readValue(body, WerknemerDTO.class);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Fout bij updaten werknemer", e);
        }
    }

    //zoeken op id
    // ====================================================================
    public WerknemerDTO zoekOpId(int id) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + id)
                .GET()
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return null;
            String body = response.body();
            if (body == null || body.isBlank()) return null;
            return mapper.readValue(body, WerknemerDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen werknemer", e);
        }
    }

    public boolean veranderStatus(int werknemerId, String actie) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + werknemerId + "/" + actie)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (Exception e) {
            throw new RuntimeException("Fout bij status wijzigen", e);
        }
    }

    public boolean veranderRol(int werknemerId, String nieuweRol) {
        try {
            java.net.http.HttpRequest request = authenticatedRequest(BASE_URL + "/" + werknemerId + "/rol?nieuweRol=" + nieuweRol)
                .PUT(java.net.http.HttpRequest.BodyPublishers.noBody())
                .build();

            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Fout bij updaten rol: Status " + response.statusCode());
                return false;
            }
            return true;
        } catch (Exception e) {
            System.err.println("Crash in veranderRol API: " + e.getMessage());
            return false;
        }
    }

    public boolean registreerWerknemer(String naam, String voornaam, String email, String telefoon, String geboortedatum, String rol) {
        try {
            String jsonBody = String.format(
                "{\"naam\":\"%s\",\"voornaam\":\"%s\",\"email\":\"%s\",\"wachtwoord\":\"Wachtwoord123\",\"telefoonnummer\":\"%s\",\"geboortedatum\":\"%s\",\"rol\":\"%s\"}",
                naam, voornaam, email, telefoon, geboortedatum, rol
            );
            HttpRequest request = authenticatedRequest(BASE_URL)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (Exception e) {
            throw new RuntimeException("Fout bij registreren werknemer", e);
        }
    }

}

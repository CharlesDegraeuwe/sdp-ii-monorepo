package domain.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.javafx.binding.StringFormatter;
import domain.dto.CreateTeamDTO;
import domain.dto.SiteDTO;
import domain.dto.TeamDTO;
import domain.dto.WerknemerDTO;
import io.github.cdimascio.dotenv.Dotenv;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class TeamApiService {
    Dotenv dotenv = Dotenv.load();
    private final String BASE_URL = dotenv.get("BASE_URL") +"/teams";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    //calls etc
    // ====================================================================
    public List<TeamDTO> getAlleTeams() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            TeamDTO[] array = mapper.readValue(response.body(), TeamDTO[].class);
            return Arrays.asList(array);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen teams", e);
        }
    }

    public List<TeamDTO> geefTeamsVanSite(int siteId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/site/" + siteId))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            return Arrays.asList(mapper.readValue(response.body(), TeamDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen teams", e);
        }
    }


    public List<WerknemerDTO> getTeamMembers(int teamId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + teamId + "/werknemers"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            WerknemerDTO[] array = mapper.readValue(response.body(), WerknemerDTO[].class);
            return Arrays.asList(array);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen teams", e);
        }
    }

    public List<WerknemerDTO> getBeschikbareWerknemers(int teamId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + teamId + "/beschikbaar"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return Arrays.asList(mapper.readValue(response.body(), WerknemerDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen beschikbare werknemers", e);
        }
    }

    public List<WerknemerDTO> voegLidToe(int teamId, int werknemerId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + teamId + "/" + werknemerId))
                    .PUT(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return Arrays.asList(mapper.readValue(response.body(), WerknemerDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij toevoegen lid", e);
        }
    }

    public List<SiteDTO> getAlleSites() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/sites"))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return Arrays.asList(mapper.readValue(response.body(), SiteDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen sites", e);
        }
    }

    public List<WerknemerDTO> getAlleWerknemers() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/werknemers"))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return Arrays.asList(mapper.readValue(response.body(), WerknemerDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen werknemers", e);
        }
    }

    public TeamDTO maakTeam(CreateTeamDTO dto) {
        try {
            String json = mapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), TeamDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij aanmaken team", e);
        }
    }

    public List<TeamDTO> getTeamsVanWerknemer(int werknemerId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/werknemer/" + werknemerId))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return Arrays.asList(mapper.readValue(response.body(), TeamDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen teams van werknemer", e);
        }
    }

    public void verwijderLid(int teamId, int werknemerId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + teamId + "/" + werknemerId))
                    .DELETE()
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Fout bij verwijderen lid", e);
        }
    }
}
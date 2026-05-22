package domain.services;

import domain.dto.*;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TeamApiService extends ApiService {
    Dotenv dotenv = Dotenv.load();
    private final String BASE_URL = dotenv.get("BASE_URL") + "/teams";

    public List<TeamDTO> getAlleTeams() {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL)
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            String body = response.body();
            if (body == null || body.isBlank()) return Collections.emptyList();
            return Arrays.asList(mapper.readValue(body, TeamDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen teams", e);
        }
    }

    public List<TeamDTO> geefTeamsVanSite(int siteId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/site/" + siteId)
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            String body = response.body();
            if (body == null || body.isBlank()) return Collections.emptyList();
            return Arrays.asList(mapper.readValue(body, TeamDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen teams", e);
        }
    }

    public List<TeamLidDTO> getTeamMembers(int teamId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + teamId + "/leden")
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            String body = response.body();
            if (body == null || body.isBlank()) return Collections.emptyList();
            return Arrays.asList(mapper.readValue(body, TeamLidDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen teamleden", e);
        }
    }

    public List<TeamLidDTO> getTeamLedenMetSupervisor(int teamId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + teamId + "/leden")
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            String body = response.body();
            if (body == null || body.isBlank()) return Collections.emptyList();
            return Arrays.asList(mapper.readValue(body, TeamLidDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen teamleden", e);
        }
    }

    public List<TeamInfoDTO> geefTeamsVanManager(int managerId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/manager/" + managerId)
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            String body = response.body();
            if (body == null || body.isBlank()) return Collections.emptyList();
            return Arrays.asList(mapper.readValue(body, TeamInfoDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen teams van manager", e);
        }
    }

    public List<WerknemerDTO> getBeschikbareWerknemers(int teamId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + teamId + "/beschikbaar")
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            String body = response.body();
            if (body == null || body.isBlank()) return Collections.emptyList();
            return Arrays.asList(mapper.readValue(body, WerknemerDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen beschikbare werknemers", e);
        }
    }

    public List<WerknemerDTO> voegLidToe(int teamId, int werknemerId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + teamId + "/" + werknemerId)
                    .PUT(HttpRequest.BodyPublishers.noBody()).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            String body = response.body();
            if (body == null || body.isBlank()) return Collections.emptyList();
            return Arrays.asList(mapper.readValue(body, WerknemerDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij toevoegen lid", e);
        }
    }

    public List<SiteDTO> getAlleSites() {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/sites")
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            String body = response.body();
            if (body == null || body.isBlank()) return Collections.emptyList();
            return Arrays.asList(mapper.readValue(body, SiteDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen sites", e);
        }
    }

    public List<WerknemerDTO> getAlleWerknemers() {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/werknemers")
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            String body = response.body();
            if (body == null || body.isBlank()) return Collections.emptyList();
            return Arrays.asList(mapper.readValue(body, WerknemerDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen werknemers", e);
        }
    }

    public TeamDTO maakTeam(CreateTeamDTO dto) {
        try {
            String json = mapper.writeValueAsString(dto);
            HttpRequest request = authenticatedRequest(BASE_URL)
                    .POST(HttpRequest.BodyPublishers.ofString(json)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300)
                throw new RuntimeException("Server fout " + response.statusCode());
            String body = response.body();
            if (body == null || body.isBlank()) return null;
            return mapper.readValue(body, TeamDTO.class);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Fout bij aanmaken team", e);
        }
    }

    public List<TeamDTO> getTeamsVanWerknemer(int werknemerId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/werknemer/" + werknemerId)
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            String body = response.body();
            if (body == null || body.isBlank()) return Collections.emptyList();
            return Arrays.asList(mapper.readValue(body, TeamDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen teams van werknemer", e);
        }
    }

    public void verwijderLid(int teamId, int werknemerId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + teamId + "/" + werknemerId)
                    .DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Fout bij verwijderen lid", e);
        }
    }

    public void verwijderTeam(int teamId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + teamId)
                    .DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Fout bij verwijderen team", e);
        }
    }

    public void maakSupervisor(int teamId, int werknemerId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + teamId + "/" + werknemerId + "/supervisor")
                    .PUT(HttpRequest.BodyPublishers.noBody()).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Fout bij supervisor aanduiden", e);
        }
    }
}

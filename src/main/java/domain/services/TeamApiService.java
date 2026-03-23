package domain.services;

import domain.dto.TeamDTO;
import domain.dto.TeamInfoDTO;
import domain.dto.WerknemerDTO;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TeamApiService extends ApiService {
    private final String BASE_URL = Dotenv.load().get("BASE_URL") + "/teams";

    public List<TeamDTO> geefTeamsVanSite(int siteId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/site/" + siteId)
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            return Arrays.asList(mapper.readValue(response.body(), TeamDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen teams", e);
        }
    }

    public List<WerknemerDTO> geefWerknemersVanTeam(int teamId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + teamId + "/werknemers")
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            return Arrays.asList(mapper.readValue(response.body(), WerknemerDTO[].class));
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen teamleden", e);
        }
    }

    // --- ONZE NIEUWE METHODE ---
    public List<TeamInfoDTO> geefTeamsVanManager(int managerId) {
        try {
            // We gebruiken jouw ingebouwde authenticatedRequest en plakken er netjes /manager/ID achter
            HttpRequest request = authenticatedRequest(BASE_URL + "/manager/" + managerId)
                    .GET().build();

            // We gebruiken de 'client' uit jouw ApiService
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) return Collections.emptyList();

            // We gebruiken de 'mapper' uit jouw ApiService
            return Arrays.asList(mapper.readValue(response.body(), TeamInfoDTO[].class));

        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen teams van manager", e);
        }
    }
}
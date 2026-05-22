package domain.services;

import domain.dto.LocatieDTO;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SiteApiService extends ApiService {
    private final String BASE_URL = Dotenv.load().get("BASE_URL") + "/sites";

    public List<LocatieDTO> geefSitesVanManager(int managerId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/manager/" + managerId)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            String body = response.body();
            if (body == null || body.isBlank()) return Collections.emptyList();
            LocatieDTO[] array = mapper.readValue(body, LocatieDTO[].class);
            return Arrays.asList(array);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen sites", e);
        }
    }
}
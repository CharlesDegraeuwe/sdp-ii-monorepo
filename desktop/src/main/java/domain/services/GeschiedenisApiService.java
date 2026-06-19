package domain.services;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.dto.GeschiedenisItemDTO;
import domain.dto.WerknemerDTO;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class GeschiedenisApiService extends ApiService {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private final String BASE_URL = dotenv.get("BASE_URL") + "/geschiedenis";

    public List<GeschiedenisItemDTO> geefGeschiedenisVanWerknemer(int werknemerId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/werknemer/" + werknemerId)
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return List.of();
            String body = response.body();
            if (body == null || body.isBlank()) return List.of();
            return mapper.readValue(body, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen geschiedenis", e);
        }
    }

    public List<WerknemerDTO> geefTeamledenVanManager(int managerId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/team/" + managerId)
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return List.of();
            String body = response.body();
            if (body == null || body.isBlank()) return List.of();
            return mapper.readValue(body, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen teamleden", e);
        }
    }
}

package domain.services;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.dto.AfwezigheidsOverzichtDTO;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

public class PlanningApiService extends ApiService {
    private final String BASE_URL = Dotenv.load().get("BASE_URL") + "/planning";

    public List<AfwezigheidsOverzichtDTO> geefAfwezighedenVanTeam(int werknemerId, LocalDate van, LocalDate tot) {
        try {
            String url = BASE_URL + "/team/" + werknemerId + "?van=" + van + "&tot=" + tot;
            HttpRequest request = authenticatedRequest(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return List.of();
            String body = response.body();
            if (body == null || body.isBlank()) return List.of();
            return mapper.readValue(body, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen teamafwezigheden", e);
        }
    }
}

package domain.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import domain.dto.AfwezigheidsOverzichtDTO;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

public class PlanningApiService {
    Dotenv dotenv = Dotenv.load();
    private final String BASE_URL = dotenv.get("BASE_URL") + "/planning";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public List<AfwezigheidsOverzichtDTO> geefAfwezighedenVanTeam(int werknemerId, LocalDate van, LocalDate tot) {
        try {
            String url = BASE_URL + "/team/" + werknemerId + "?van=" + van + "&tot=" + tot;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), new TypeReference<List<AfwezigheidsOverzichtDTO>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen afwezigheidsoverzicht", e);
        }
    }
}
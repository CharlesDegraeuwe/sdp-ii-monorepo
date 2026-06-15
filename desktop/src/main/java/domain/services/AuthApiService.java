package domain.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import domain.auth.Sessie;
import domain.dto.WerknemerDTO;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthApiService {
    Dotenv dotenv = Dotenv.load();
    private final String BASE_URL = dotenv.get("BASE_URL")+"/werknemers";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public WerknemerDTO login(String email, String wachtwoord) {
        System.out.println(BASE_URL + "/login");
        try {
            String json = """
                {"email": "%s", "wachtwoord": "%s"}
                """.formatted(email, wachtwoord);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/login"))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            WerknemerDTO werknemer = mapper.readValue(response.body(), WerknemerDTO.class);
            return werknemer;
        } catch (Exception e) {
            throw new RuntimeException("Fout bij inloggen", e);
        }
    }
}

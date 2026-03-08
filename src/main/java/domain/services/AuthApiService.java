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

public class AuthApiService extends ApiService {
    Dotenv dotenv = Dotenv.load();
    private final String BASE_URL = dotenv.get("BASE_URL")+"/werknemers";


    public WerknemerDTO login(String email, String wachtwoord) {
        try {
            String json = """
                {"email": "%s", "wachtwoord": "%s"}
                """.formatted(email, wachtwoord);

            HttpRequest request = authenticatedRequest(BASE_URL + "/login")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();


            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            response.headers().firstValue("Set-Cookie").ifPresent(cookie -> {
                if (cookie.contains("JSESSIONID=")) {
                    String sessionId = cookie.split("JSESSIONID=")[1].split(";")[0];
                    Sessie.getInstance().setSessionId(sessionId);
                }
            });
            System.out.println("Status: " + response.statusCode());
            System.out.println("Body: " + response.body());

            return mapper.readValue(response.body(), WerknemerDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij inloggen", e);
        }
    }
}

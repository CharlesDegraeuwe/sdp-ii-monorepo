package domain.services;

import domain.auth.Sessie;
import domain.dto.WerknemerDTO;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthApiService extends ApiService {
    private final String BASE_URL = Dotenv.load().get("BASE_URL") + "/werknemers";

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

            return mapper.readValue(response.body(), WerknemerDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij inloggen", e);
        }
    }

    public boolean activeerAccount(int werknemerId, String activatieCode) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + werknemerId + "/activeer?code=" + activatieCode)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200 && !response.body().contains("Fout");
        } catch (Exception e) {
            throw new RuntimeException("Fout bij activeren", e);
        }
    }
}
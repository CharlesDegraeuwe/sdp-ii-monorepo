package domain.services;

import domain.auth.Sessie;
import domain.dto.LoginResponseDTO;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthApiService extends ApiService {
    private final String BASE_URL = Dotenv.load().get("BASE_URL") + "/werknemers";

    public void verzendLoginEmail(String email) {
        try {
            String json = """
                {"email": "%s"}
                """.formatted(email);

            HttpRequest request = authenticatedRequest(BASE_URL + "/login-mail")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 204 && response.statusCode() != 200) {
                throw new RuntimeException("Fout bij verzenden email: " + response.statusCode());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Fout bij verzenden email", e);
        }
    }

    public LoginResponseDTO loginMetWachtwoord(String email, String wachtwoord) {
        try {
            String json = """
                {"email": "%s", "wachtwoord": "%s"}
                """.formatted(email, wachtwoord);

            HttpRequest request = authenticatedRequest(BASE_URL + "/login-password")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Ongeldig email of wachtwoord");
            }

            String body = response.body();
            if (body == null || body.isBlank()) throw new RuntimeException("Leeg antwoord van server");
            LoginResponseDTO result = mapper.readValue(body, LoginResponseDTO.class);
            Sessie.getInstance().setJwtToken(result.token());
            return result;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Fout bij inloggen", e);
        }
    }

    public LoginResponseDTO loginMetCode(String email, String code) {
        try {
            String json = """
                {"email": "%s", "token": "%s"}
                """.formatted(email, code);

            HttpRequest request = authenticatedRequest(BASE_URL + "/login-token")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Ongeldige code of email");
            }

            String body = response.body();
            if (body == null || body.isBlank()) throw new RuntimeException("Leeg antwoord van server");
            LoginResponseDTO result = mapper.readValue(body, LoginResponseDTO.class);
            Sessie.getInstance().setJwtToken(result.token());
            return result;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Fout bij inloggen", e);
        }
    }
}

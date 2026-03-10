package domain.facades;

import domain.auth.Sessie;
import domain.dto.WerknemerDTO;
import domain.services.AuthApiService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthFacade {
    private final AuthApiService api = new AuthApiService();
    public WerknemerDTO login(String email, String wachtwoord) {
        WerknemerDTO werknemer = api.login(email, wachtwoord);
        Sessie.getInstance().setIngelogdeWerknemer(werknemer);
        return werknemer;
    }

    public boolean activeerAccount(int werknemerId, String activatieCode) {
        try {
            String url = "http://localhost:8080/api/werknemers/" + werknemerId + "/activeer?code=" + activatieCode;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String antwoordVanServer = response.body();

            if (response.statusCode() == 200 && !antwoordVanServer.contains("Fout")) {
                return true;
            } else {
                System.out.println("Server weigerde de code: " + antwoordVanServer);
                return false;
            }

        } catch (Exception e) {
            System.err.println("Fout bij activeren: " + e.getMessage());
            return false;
        }
    }
}

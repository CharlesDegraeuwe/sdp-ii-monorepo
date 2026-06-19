package domain.services;

import domain.dto.AfwezigheidAanmakenDTO;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AfwezigheidApiService extends ApiService {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private final String BASE_URL = dotenv.get("BASE_URL") + "/afwezigheid";

    public String meldAfwezigheid(AfwezigheidAanmakenDTO dto) {
        try {
            String json = mapper.writeValueAsString(dto);

            HttpRequest request = authenticatedRequest(BASE_URL)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300)
                throw new RuntimeException("Server fout " + response.statusCode());
            String body = response.body();
            return body != null ? body : "";
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Fout bij melden afwezigheid", e);
        }
    }
}

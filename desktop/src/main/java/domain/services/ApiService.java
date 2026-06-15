package domain.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import domain.auth.Sessie;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public abstract class ApiService {
    protected final HttpClient client = HttpClient.newHttpClient();
    protected final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    protected HttpRequest.Builder authenticatedRequest(String url) {
        var builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json");

        String jwtToken = Sessie.getInstance().getJwtToken();
        if (jwtToken != null) {
            builder.header("Authorization", "Bearer " + jwtToken);
        }

        return builder;
    }
}

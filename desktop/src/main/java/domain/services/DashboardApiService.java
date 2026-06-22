package domain.services;

import domain.dto.ActiviteitLogDTO;
import io.github.cdimascio.dotenv.Dotenv;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class DashboardApiService extends ApiService {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private final String BASE_URL = dotenv.get("BASE_URL");

    public int getTotaalWerknemers() {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/werknemers/totaal").GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return 0;
            return Integer.parseInt(response.body());
        } catch (Exception e) { return 0; }
    }

    public int getActieveSitesPercentage() {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/sites/actief-percentage").GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return 0;
            return Integer.parseInt(response.body());
        } catch (Exception e) { return 0; }
    }

    public int getAfwezigeWerknemers() {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/afwezigheid/huidig").GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return 0;
            return Integer.parseInt(response.body());
        } catch (Exception e) { return 0; }
    }

    public List<ActiviteitLogDTO> getRecenteLogs() {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/logs/recent").GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return List.of();
            return Arrays.asList(mapper.readValue(response.body(), ActiviteitLogDTO[].class));
        } catch (Exception e) { return List.of(); }
    }
}

package domain.services;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.dto.ShiftDTO;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ShiftApiService extends ApiService {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private final String BASE_URL = dotenv.get("BASE_URL") + "/shifts";

    public List<ShiftDTO> geefShiftenVanWerknemerBereik(int werknemerId, LocalDate van, LocalDate tot) {
        try {
            String url = BASE_URL + "/werknemer/" + werknemerId + "/bereik?van=" + van + "&tot=" + tot;
            HttpRequest request = authenticatedRequest(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return List.of();
            String body = response.body();
            if (body == null || body.isBlank()) return List.of();
            return mapper.readValue(body, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen shifts", e);
        }
    }

    public record ShiftAanmakenRequest(
        int werknemerId,
        String startDatum,
        String eindDatum,
        String startTijd,
        String eindTijd,
        String pauzeStart,
        String pauzeEind
    ) {}

    public void maakShiftAan(int werknemerId, LocalDate datum, LocalTime start, LocalTime eind, LocalTime pauzeStart, LocalTime pauzeEind) {
        try {
            LocalDate eindDatum = eind.isBefore(start) ? datum.plusDays(1) : datum;

            ShiftAanmakenRequest req = new ShiftAanmakenRequest(
                werknemerId,
                datum.toString(),
                eindDatum.toString(),
                start.toString(),
                eind.toString(),
                pauzeStart != null ? pauzeStart.toString() : null,
                pauzeEind != null ? pauzeEind.toString() : null
            );

            String json = mapper.writeValueAsString(req);

            HttpRequest request = authenticatedRequest(BASE_URL)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            // --- HARDCORE DEBUG LIJNEN ---
            System.out.println("==== DEBUG FRONTEND VERZENDT ====");
            System.out.println("Naar URL: " + request.uri());
            System.out.println("Methode: " + request.method());
            System.out.println("Headers: " + request.headers().map());
            System.out.println("Body: " + json);
            System.out.println("=================================");
            // -----------------------------

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                System.out.println("CRASH IN BACKEND: " + response.body());
                throw new RuntimeException("Server fout " + response.statusCode() + ": " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Kon shift niet opslaan: " + e.getMessage(), e);
        }
    }
}

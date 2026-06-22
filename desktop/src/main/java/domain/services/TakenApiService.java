package domain.services;

import domain.dto.TaakDTO;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TakenApiService extends ApiService {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private final String BASE_URL = dotenv.get("BASE_URL") + "/taken";

    public List<TaakDTO> geefTakenVanWerknemer(int werknemerId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/werknemer/" + werknemerId)
                .GET()
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            String body = response.body();
            if (body == null || body.isBlank()) return Collections.emptyList();
            TaakDTO[] array = mapper.readValue(body, TaakDTO[].class);
            return Arrays.asList(array);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen taken", e);
        }
    }

    public List<TaakDTO> geefAlleTaken() {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/alle").GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return Collections.emptyList();
            String body = response.body();
            if (body == null || body.isBlank()) return Collections.emptyList();
            TaakDTO[] array = mapper.readValue(body, TaakDTO[].class);
            return Arrays.asList(array);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen alle taken", e);
        }
    }

    // ========================================================
    // NIEUW: Stuurt de lijst van ID's als JSON naar de backend
    // ========================================================
    public String updateTaakToewijzingen(int taakId, List<Integer> werknemerIds) {
        try {
            // Zet de Java List om naar een JSON array (bijv: "[1, 2, 5]")
            String json = mapper.writeValueAsString(werknemerIds);

            HttpRequest request = authenticatedRequest(BASE_URL + "/" + taakId + "/toewijzingen")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("Server fout " + response.statusCode() + ": " + response.body());
            }

            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Fout bij updaten toewijzingen", e);
        }
    }

    public String maakTaakAan(String titel, String beschrijving, LocalDate deadline, int siteId, String startuur, String einduur) {
        try {
            int werknemerId = domain.auth.Sessie.getInstance().getIngelogdeWerknemer().id();

            String json = mapper.writeValueAsString(new TaakAanmakenRequest(werknemerId, titel, beschrijving, deadline.toString(), siteId, startuur, einduur));

            HttpRequest request = authenticatedRequest(BASE_URL)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                System.out.println("CRASH IN BACKEND: " + response.body());
                throw new RuntimeException("Server fout " + response.statusCode() + ": " + response.body());
            }
            return "Taak succesvol aangemaakt!";
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fout bij aanmaken taak", e);
        }
    }

    private record TaakAanmakenRequest(int werknemerId, String titel, String beschrijving, String deadline, int siteId, String startuur, String einduur) {}

    public String wijzigTaak(int taakId, String titel, String beschrijving) {
        try {
            String json = mapper.writeValueAsString(new TaakWijzigenRequest(titel, beschrijving));
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + taakId)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Fout bij wijzigen taak", e);
        }
    }

    private record TaakWijzigenRequest(String titel, String beschrijving) {}

    public String verwijderTaak(int taakId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + taakId)
                .DELETE()
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Fout bij verwijderen taak", e);
        }
    }

    public String markeerAfgewerkt(int taakId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + taakId + "/afgewerkt")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException("Fout bij markeren als afgewerkt", e);
        }
    }

    public void planTaakIn(int taakId, String datum, String startuur, String einduur) {
        System.out.println("=== START INPLANNEN TAAK " + taakId + " ===");
        try {
            String url = BASE_URL + "/" + taakId + "/inplannen?datum=" + datum + "&startuur=" + startuur + "&einduur=" + einduur;
            System.out.println("1. Frontend verstuurt PUT naar: " + url);

            HttpRequest request = authenticatedRequest(url).PUT(HttpRequest.BodyPublishers.noBody()).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("2. Backend antwoord code: " + response.statusCode());
            System.out.println("3. Backend antwoord body: " + response.body());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                System.err.println("❌ CRASH: Backend weigert de taak in te plannen!");
            } else {
                System.out.println("✅ SUCCES: Taak is geüpdatet in de database!");
            }
        } catch (Exception e) {
            System.err.println("❌ CRASH: Frontend kon de request niet versturen: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=========================================");
    }
}

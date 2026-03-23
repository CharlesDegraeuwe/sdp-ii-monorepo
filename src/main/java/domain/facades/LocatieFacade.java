package domain.facades;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import domain.dto.LocatieDTO;
import domain.dto.MachineAanmaakDTO;
import domain.services.LogService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class LocatieFacade {

    private final String API_URL = "http://localhost:8080/api/sites";
    private final ObjectMapper mapper = new ObjectMapper();

    public List<LocatieDTO> geefAlleLocaties() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String jsonBody = response.body();
                return mapper.readValue(jsonBody, new TypeReference<List<LocatieDTO>>() {});

            } else {
                System.err.println("Fout bij ophalen locaties. Statuscode: " + response.statusCode());
                return new ArrayList<>();
            }

        } catch (Exception e) {
            System.err.println("Kan de server niet bereiken: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public LocatieDTO vindLocatie(Integer id) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/" + id))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), LocatieDTO.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            System.err.println("Kan locatie niet ophalen: " + e.getMessage());
            return null;
        }
    }

    public boolean verwijderLocatie(Integer id) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/" + id))
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            boolean succes = response.statusCode() == 200 || response.statusCode() == 204;
            if (succes) LogService.log("DELETE", "locatie", "Locatie verwijderd – id: " + id);
            return succes;

        } catch (Exception e) {
            System.err.println("Fout bij verwijderen: " + e.getMessage());
            return false;
        }
    }

    // 2. De methode om te WIJZIGEN
    public boolean wijzigLocatie(Integer id, LocatieDTO gewijzigdeLocatie) {
        try {
            String jsonBody = mapper.writeValueAsString(gewijzigdeLocatie);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/" + id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            boolean succes = response.statusCode() == 200;
            if (succes) LogService.log("UPDATE", "locatie", "Locatie gewijzigd – id: " + id + ", naam: " + gewijzigdeLocatie.naam());
            return succes;

        } catch (Exception e) {
            System.err.println("Fout bij wijzigen: " + e.getMessage());
            return false;
        }
    }

    public boolean maakLocatie(LocatieDTO nieuweLocatie) {
        try {
            String jsonBody = mapper.writeValueAsString(nieuweLocatie);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            boolean succes = response.statusCode() == 200 || response.statusCode() == 201;
            if (succes) LogService.log("CREATE", "locatie", "Locatie aangemaakt – naam: " + nieuweLocatie.naam() + ", locatie: " + nieuweLocatie.locatie());
            return succes;

        } catch (Exception e) {
            System.err.println("Fout bij aanmaken: " + e.getMessage());
            return false;
        }
    }

    public boolean maakMachine(MachineAanmaakDTO dto) {
        try {
            String jsonBody = mapper.writeValueAsString(dto);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/machines"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            boolean succes = response.statusCode() == 200 || response.statusCode() == 201;
            if (succes) LogService.log("CREATE", "machine", "Machine aangemaakt – siteId: " + dto.siteId());
            return succes;

        } catch (Exception e) {
            System.err.println("Fout bij aanmaken machine: " + e.getMessage());
            return false;
        }
    }

    public List<MachineAanmaakDTO> haalMachinesOpVoorSite(Integer siteId) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/" + siteId + "/machines"))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<MachineAanmaakDTO>>() {});
            }
        } catch (Exception e) {
            System.err.println("Fout bij ophalen machines: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public boolean wijzigMachine(Integer machineId, MachineAanmaakDTO dto) {
        try {
            String jsonBody = mapper.writeValueAsString(dto);
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/machines/" + machineId))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            boolean succes = response.statusCode() == 200;
            if (succes) LogService.log("UPDATE", "machine", "Machine gewijzigd – machineId: " + machineId);
            return succes;
        } catch (Exception e) {
            System.err.println("Fout bij wijzigen machine: " + e.getMessage());
            return false;
        }
    }

    public boolean verwijderMachine(Integer machineId) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/machines/" + machineId))
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            boolean succes = response.statusCode() == 200 || response.statusCode() == 204;
            if (succes) LogService.log("DELETE", "machine", "Machine verwijderd – machineId: " + machineId);
            return succes;

        } catch (Exception e) {
            System.err.println("Fout bij verwijderen machine: " + e.getMessage());
            return false;
        }
    }
}

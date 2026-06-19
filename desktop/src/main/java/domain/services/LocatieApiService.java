package domain.services;

import com.fasterxml.jackson.core.type.TypeReference;
import domain.dto.LocatieDTO;
import domain.dto.MachineAanmaakDTO;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class LocatieApiService extends ApiService {
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private final String BASE_URL = dotenv.get("BASE_URL") + "/sites";
    private final String MACHINES_URL = dotenv.get("BASE_URL") + "/machines";

    public List<LocatieDTO> geefAlleLocaties() {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return new ArrayList<>();
            return mapper.readValue(response.body(), new TypeReference<List<LocatieDTO>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen locaties", e);
        }
    }

    public LocatieDTO vindLocatie(Integer id) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + id)
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return null;
            return mapper.readValue(response.body(), LocatieDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen locatie", e);
        }
    }

    public boolean verwijderLocatie(Integer id) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + id)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            boolean succes = response.statusCode() == 200 || response.statusCode() == 204;
            if (succes) LogService.log("DELETE", "locatie", "Locatie verwijderd – id: " + id);
            return succes;
        } catch (Exception e) {
            throw new RuntimeException("Fout bij verwijderen locatie", e);
        }
    }

    public boolean wijzigLocatie(Integer id, LocatieDTO gewijzigdeLocatie) {
        try {
            String json = mapper.writeValueAsString(gewijzigdeLocatie);
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + id)
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            boolean succes = response.statusCode() == 200;
            if (succes) LogService.log("UPDATE", "locatie", "Locatie gewijzigd – id: " + id + ", naam: " + gewijzigdeLocatie.naam());
            return succes;
        } catch (Exception e) {
            throw new RuntimeException("Fout bij wijzigen locatie", e);
        }
    }

    public boolean maakLocatie(LocatieDTO nieuweLocatie) {
        try {
            String json = mapper.writeValueAsString(nieuweLocatie);
            HttpRequest request = authenticatedRequest(BASE_URL)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            boolean succes = response.statusCode() == 200 || response.statusCode() == 201;
            if (succes) LogService.log("CREATE", "locatie", "Locatie aangemaakt – naam: " + nieuweLocatie.naam());
            return succes;
        } catch (Exception e) {
            throw new RuntimeException("Fout bij aanmaken locatie", e);
        }
    }

    // ─── MACHINES ────────────────────────────────────────────────────────────────

    public List<MachineAanmaakDTO> haalMachinesOpVoorSite(Integer siteId) {
        try {
            HttpRequest request = authenticatedRequest(BASE_URL + "/" + siteId + "/machines")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return new ArrayList<>();
            return mapper.readValue(response.body(), new TypeReference<List<MachineAanmaakDTO>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Fout bij ophalen machines", e);
        }
    }

    public boolean maakMachine(MachineAanmaakDTO dto) {
        try {
            String json = mapper.writeValueAsString(dto);
            HttpRequest request = authenticatedRequest(MACHINES_URL)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            boolean succes = response.statusCode() == 200 || response.statusCode() == 201;
            if (succes) LogService.log("CREATE", "machine", "Machine aangemaakt – siteId: " + dto.siteId());
            return succes;
        } catch (Exception e) {
            throw new RuntimeException("Fout bij aanmaken machine", e);
        }
    }

    public boolean wijzigMachine(Integer machineId, MachineAanmaakDTO dto) {
        try {
            String json = mapper.writeValueAsString(dto);
            HttpRequest request = authenticatedRequest(MACHINES_URL + "/" + machineId)
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            boolean succes = response.statusCode() == 200;
            if (succes) LogService.log("UPDATE", "machine", "Machine gewijzigd – machineId: " + machineId);
            return succes;
        } catch (Exception e) {
            throw new RuntimeException("Fout bij wijzigen machine", e);
        }
    }

    public boolean verwijderMachine(Integer machineId) {
        try {
            HttpRequest request = authenticatedRequest(MACHINES_URL + "/" + machineId)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            boolean succes = response.statusCode() == 200 || response.statusCode() == 204;
            if (succes) LogService.log("DELETE", "machine", "Machine verwijderd – machineId: " + machineId);
            return succes;
        } catch (Exception e) {
            throw new RuntimeException("Fout bij verwijderen machine", e);
        }
    }
}

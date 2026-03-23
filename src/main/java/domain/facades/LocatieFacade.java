package domain.facades;

import domain.dto.LocatieDTO;
import domain.dto.MachineAanmaakDTO;
import domain.services.LocatieApiService;

import java.util.List;

public class LocatieFacade {
    private LocatieApiService api = new LocatieApiService();

    public List<LocatieDTO> geefAlleLocaties() {
        return api.geefAlleLocaties();
    }

    public LocatieDTO vindLocatie(Integer id) {
        return api.vindLocatie(id);
    }

    public boolean verwijderLocatie(Integer id) {
        return api.verwijderLocatie(id);
    }

    public boolean wijzigLocatie(Integer id, LocatieDTO gewijzigdeLocatie) {
        valideerLocatie(gewijzigdeLocatie);
        return api.wijzigLocatie(id, gewijzigdeLocatie);
    }

    public boolean maakLocatie(LocatieDTO nieuweLocatie) {
        valideerLocatie(nieuweLocatie);
        return api.maakLocatie(nieuweLocatie);
    }

    // ─── MACHINES ────────────────────────────────────────────────────────────────

    public boolean maakMachine(MachineAanmaakDTO dto) {
        if (dto.naam() == null || dto.naam().isBlank())
            throw new IllegalArgumentException("Machine naam is verplicht.");
        if (dto.status() == null || dto.status().isBlank())
            throw new IllegalArgumentException("Machine status is verplicht.");
        return api.maakMachine(dto);
    }

    public List<MachineAanmaakDTO> haalMachinesOpVoorSite(Integer siteId) {
        return api.haalMachinesOpVoorSite(siteId);
    }

    public boolean wijzigMachine(Integer machineId, MachineAanmaakDTO dto) {
        if (dto.naam() == null || dto.naam().isBlank())
            throw new IllegalArgumentException("Machine naam is verplicht.");
        return api.wijzigMachine(machineId, dto);
    }

    public boolean verwijderMachine(Integer machineId) {
        return api.verwijderMachine(machineId);
    }

    private void valideerLocatie(LocatieDTO locatie) {
        if (locatie.naam() == null || locatie.naam().isBlank())
            throw new IllegalArgumentException("Locatienaam is verplicht.");
        if (locatie.locatie() == null || locatie.locatie().isBlank())
            throw new IllegalArgumentException("Adres/locatie is verplicht.");
        if (locatie.capaciteit() == null || locatie.capaciteit() <= 0)
            throw new IllegalArgumentException("Capaciteit moet een positief getal zijn.");
        if (locatie.status() == null || locatie.status().isBlank())
            throw new IllegalArgumentException("Status is verplicht.");
    }
}
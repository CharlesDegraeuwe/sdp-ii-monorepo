package domain.facades;

import domain.auth.Sessie;
import domain.dto.ShiftDTO;
import domain.services.ShiftApiService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ShiftFacade {
    private final ShiftApiService api = new ShiftApiService();

    public List<ShiftDTO> geefShiftenVanWerknemerBereik(int werknemerId, LocalDate van, LocalDate tot) {
        return api.geefShiftenVanWerknemerBereik(werknemerId, van, tot);
    }

    public void maakShiftAan(int werknemerId, LocalDate datum, LocalTime start, LocalTime eind, LocalTime pauzeStart, LocalTime pauzeEind) {

        if (!Sessie.getInstance().isMangerOrAdmin()) {
            throw new RuntimeException("Toegang geweigerd: Alleen managers of admins mogen shifts inplannen.");
        }

        api.maakShiftAan(werknemerId, datum, start, eind, pauzeStart, pauzeEind);
    }
}

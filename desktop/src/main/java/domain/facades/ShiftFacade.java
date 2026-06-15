package domain.facades;

import domain.dto.ShiftDTO;
import domain.services.ShiftApiService;

import java.time.LocalDate;
import java.util.List;

public class ShiftFacade {
    private final ShiftApiService api = new ShiftApiService();

    public List<ShiftDTO> geefShiftenVanWerknemerBereik(int werknemerId, LocalDate van, LocalDate tot) {
        return api.geefShiftenVanWerknemerBereik(werknemerId, van, tot);
    }
}

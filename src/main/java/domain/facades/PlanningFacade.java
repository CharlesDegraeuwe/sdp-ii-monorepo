package domain.facades;

import domain.dto.AfwezigheidsOverzichtDTO;
import domain.services.PlanningApiService;

import java.time.LocalDate;
import java.util.List;

public class PlanningFacade {
    private final PlanningApiService api = new PlanningApiService();

    public List<AfwezigheidsOverzichtDTO> geefAfwezighedenVanTeam(int werknemerId, LocalDate van, LocalDate tot) {
        return api.geefAfwezighedenVanTeam(werknemerId, van, tot);
    }
}
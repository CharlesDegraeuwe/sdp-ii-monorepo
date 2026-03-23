package domain.facades;

import domain.dto.AfwezigheidsOverzichtDTO;
import domain.dto.TeamDTO;
import domain.services.PlanningApiService;

import java.time.LocalDate;
import java.util.List;

public class PlanningFacade {
    private PlanningApiService api = new PlanningApiService();

    // Wordt gebruikt door een 'gewone' werknemer (zoekt op basis van zijn eigen ID)
    public List<AfwezigheidsOverzichtDTO> geefAfwezighedenVanTeam(int werknemerId, LocalDate van, LocalDate tot) {
        return api.geefAfwezighedenVanTeam(werknemerId, van, tot);
    }

    public List<AfwezigheidsOverzichtDTO> geefAlleAfwezigheden(LocalDate van, LocalDate tot) {
        return api.geefAlleAfwezigheden(van, tot);
    }

    public List<AfwezigheidsOverzichtDTO> geefAfwezighedenVanSpecifiekTeam(int teamId, LocalDate van, LocalDate tot) {
        return api.geefAfwezighedenVanSpecifiekTeam(teamId, van, tot);
    }

}
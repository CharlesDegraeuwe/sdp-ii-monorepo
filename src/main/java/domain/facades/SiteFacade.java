package domain.facades;

import domain.auth.Sessie;
import domain.dto.LocatieDTO;
import domain.services.SiteApiService;

import java.util.List;

public class SiteFacade {
    private final SiteApiService api = new SiteApiService();

    public List<LocatieDTO> geefSitesVanManager() {
        int managerId = Sessie.getInstance().getIngelogdeWerknemer().id();
        return api.geefSitesVanManager(managerId);
    }
}

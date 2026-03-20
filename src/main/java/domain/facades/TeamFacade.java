package domain.facades;

import domain.dto.TeamDTO;
import domain.dto.WerknemerDTO;
import domain.services.TeamApiService;

import java.util.List;

public class TeamFacade {
    private final TeamApiService api = new TeamApiService();

    public List<TeamDTO> geefTeamsVanSite(int siteId) {
        return api.geefTeamsVanSite(siteId);
    }

    public List<WerknemerDTO> geefWerknemersVanTeam(int teamId) {
        return api.geefWerknemersVanTeam(teamId);
    }
}

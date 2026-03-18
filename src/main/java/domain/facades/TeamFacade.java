package domain.facades;

import domain.dto.TeamDTO;
import domain.services.TeamApiService;

import java.util.List;

public class TeamFacade {
    private final TeamApiService api = new TeamApiService();

    public List<TeamDTO> getAlleTeams() {
        return api.getAlleTeams();
    }
}

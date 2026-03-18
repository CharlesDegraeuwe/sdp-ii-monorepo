package domain.facades;

import domain.services.TeamApiService;

public class TeamFacade {
    private final TeamApiService api = new TeamApiService();

    public String bekijkTeams() {
        return api.geefTeamsTerug();
    }
}

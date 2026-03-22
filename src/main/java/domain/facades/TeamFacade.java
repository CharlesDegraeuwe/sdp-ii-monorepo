package domain.facades;

import domain.dto.CreateTeamDTO;
import domain.dto.SiteDTO;
import domain.dto.TeamDTO;
import domain.dto.WerknemerDTO;
import domain.services.TeamApiService;

import java.util.List;

public class TeamFacade {
    private final TeamApiService api = new TeamApiService();


    public List<TeamDTO> getAlleTeams() {
        return api.getAlleTeams();
    }

    public List<WerknemerDTO> getTeamLeden(int teamID) {
        return api.getTeamMembers(teamID);
    }

    public List<TeamDTO> geefTeamsVanSite(int siteId) {
        return api.geefTeamsVanSite(siteId);
    }

    public List<WerknemerDTO> geefWerknemersVanTeam(int teamId) {
        return api.getTeamMembers(teamId);
    }

    public List<WerknemerDTO> getBeschikbareWerknemers(int teamId) {
        return api.getBeschikbareWerknemers(teamId);
    }

    public List<WerknemerDTO> voegLidToe(int teamId, int werknemerId) {
        return api.voegLidToe(teamId, werknemerId);
    }

    public List<SiteDTO> getAlleSites() {
        return api.getAlleSites();
    }

    public List<WerknemerDTO> getAlleWerknemersVoorTeams() {
        return api.getAlleWerknemers();
    }

    public TeamDTO maakTeam(CreateTeamDTO dto) {
        return api.maakTeam(dto);
    }

    public List<TeamDTO> getTeamsVanWerknemer(int werknemerId) {
        return api.getTeamsVanWerknemer(werknemerId);
    }
}


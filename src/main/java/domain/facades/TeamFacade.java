package domain.facades;

import domain.dto.*;
import domain.services.TeamApiService;

import java.util.List;

public class TeamFacade {
    private final TeamApiService api = new TeamApiService();

    public List<TeamDTO> getAlleTeams() {
        return api.getAlleTeams();
    }

    public List<TeamLidDTO> getTeamLeden(int teamId) {
        return api.getTeamMembers(teamId);
    }

    public List<TeamDTO> geefTeamsVanSite(int siteId) {
        return api.geefTeamsVanSite(siteId);
    }

    public List<TeamLidDTO> geefWerknemersVanTeam(int teamId) {
        return api.getTeamMembers(teamId);
    }


    public List<TeamInfoDTO> geefTeamsVanManager(int managerId) {
        return api.geefTeamsVanManager(managerId);
    }

    public List<TeamLidDTO> geefTeamLedenMetSupervisor(int teamId) {
        return api.getTeamLedenMetSupervisor(teamId);
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
        if (dto.naam() == null || dto.naam().isBlank())
            throw new IllegalArgumentException("Teamnaam is verplicht.");
        if (dto.managerId() == null)
            throw new IllegalArgumentException("Selecteer een manager.");
        if (dto.siteId() == null)
            throw new IllegalArgumentException("Selecteer een site.");
        if (dto.leden() == null || dto.leden().isEmpty())
            throw new IllegalArgumentException("Selecteer minstens één teamlid.");
        if (dto.leden().size() > 4)
            throw new IllegalArgumentException("Een team mag maximaal 4 leden hebben.");

        long aantalSupervisors = dto.leden().stream()
                .filter(l -> l.isSupervisor())
                .count();
        if (aantalSupervisors > 1) {
            throw new IllegalArgumentException("Een team mag maximaal één supervisor hebben.");
        }
        return api.maakTeam(dto);
    }

    public List<TeamDTO> getTeamsVanWerknemer(int werknemerId) {
        return api.getTeamsVanWerknemer(werknemerId);
    }

    public void verwijderLid(int teamId, int werknemerId) {
        api.verwijderLid(teamId, werknemerId);
    }

    public void verwijderTeam(int teamId) {
        api.verwijderTeam(teamId);
    }

    public void maakSupervisor(int teamId, int werknemerId) {
        api.maakSupervisor(teamId, werknemerId);
    }
}
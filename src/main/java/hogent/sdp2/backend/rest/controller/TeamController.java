package hogent.sdp2.backend.rest.controller;

import hogent.sdp2.backend.rest.dto.request.CreateTeamRequestDTO;
import hogent.sdp2.backend.rest.dto.request.TeamResponseDTO;
import hogent.sdp2.backend.rest.dto.response.SiteResponseDTO;
import hogent.sdp2.backend.rest.dto.response.TeamLidResponseDTO;
import hogent.sdp2.backend.rest.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.rest.service.teams.TeamService;
import hogent.sdp2.backend.auth.SessieService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final SessieService sessieService;

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @GetMapping
    public List<TeamResponseDTO> geefTeams() {
        return teamService.geefTeams();
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @GetMapping("/site/{siteId}")
    public List<TeamResponseDTO> geefTeamsVanSite(@PathVariable Integer siteId) {
        return teamService.geefTeamsVanSite(siteId);
    }

    @GetMapping("/{teamId}/werknemers")
    public List<WerknemerResponseDTO> geefWerknemersVanTeam(@PathVariable Integer teamId) {
        sessieService.assertToegangTotTeam(teamId);
        return teamService.geefWerknemersVanTeam(teamId);
    }

    @GetMapping("/{teamId}/leden")
    public List<TeamLidResponseDTO> geefTeamLeden(@PathVariable Integer teamId) {
        sessieService.assertToegangTotTeam(teamId);
        return teamService.geefTeamLedenMetSupervisor(teamId);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @GetMapping("/{teamId}/beschikbaar")
    public List<WerknemerResponseDTO> geefBeschikbaar(@PathVariable Integer teamId) {
        return teamService.geefBeschikbareWerknemers(teamId);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @GetMapping("/werknemers")
    public List<WerknemerResponseDTO> geefAlleWerknemers() {
        return teamService.geefAlleWerknemers();
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @GetMapping("/sites")
    public List<SiteResponseDTO> geefAlleSites() {
        return teamService.geefAlleSites();
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @PostMapping
    public TeamResponseDTO maakTeam(@RequestBody CreateTeamRequestDTO dto) {
        return teamService.maakTeam(dto);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @PutMapping("/{teamId}/{werknemerId}/supervisor")
    public void maakSupervisor(@PathVariable Integer teamId, @PathVariable Integer werknemerId) {
        teamService.maakSupervisor(teamId, werknemerId);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @PutMapping("/{teamId}/{werknemerId}")
    public List<WerknemerResponseDTO> voegToeAanTeam(@PathVariable Integer teamId, @PathVariable Integer werknemerId) {
        return teamService.voegToeAanTeam(teamId, werknemerId);
    }

    @GetMapping("/werknemer/{werknemerId}")
    public List<TeamResponseDTO> geefTeamsVanWerknemer(@PathVariable Integer werknemerId) {
        sessieService.assertToegangTotWerknemer(werknemerId);
        return teamService.geefTeamsVanWerknemer(werknemerId);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @DeleteMapping("/{teamId}/{werknemerId}")
    public List<WerknemerResponseDTO> verwijderUitTeam(@PathVariable Integer teamId, @PathVariable Integer werknemerId) {
        return teamService.verwijderUitTeam(teamId, werknemerId);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @DeleteMapping("/{teamId}")
    public List<TeamResponseDTO> verwijderTeam(@PathVariable Integer teamId) {
        return teamService.verwijderTeam(teamId);
    }
}

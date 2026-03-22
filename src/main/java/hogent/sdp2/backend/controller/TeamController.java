package hogent.sdp2.backend.controller;

import hogent.sdp2.backend.dto.request.CreateTeamRequestDTO;
import hogent.sdp2.backend.dto.request.TeamResponseDTO;
import hogent.sdp2.backend.dto.response.SiteResponseDTO;
import hogent.sdp2.backend.dto.response.TeamLidResponseDTO;
import hogent.sdp2.backend.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping
    public List<TeamResponseDTO> geefTeams() {
        return teamService.geefTeams();
    }

    @GetMapping("/site/{siteId}")
    public List<TeamResponseDTO> geefTeamsVanSite(@PathVariable Integer siteId) {
        return teamService.geefTeamsVanSite(siteId);
    }

    @GetMapping("/{teamId}/werknemers")
    public List<WerknemerResponseDTO> geefWerknemersVanTeam(@PathVariable Integer teamId) {
        return teamService.geefWerknemersVanTeam(teamId);
    }

    @GetMapping("/{teamId}/leden")
    public List<TeamLidResponseDTO> geefTeamLeden(@PathVariable Integer teamId) {
        return teamService.geefTeamLedenMetSupervisor(teamId);
    }

    @GetMapping("/{teamId}/beschikbaar")
    public List<WerknemerResponseDTO> geefBeschikbaar(@PathVariable Integer teamId) {
        return teamService.geefBeschikbareWerknemers(teamId);
    }

    @GetMapping("/werknemers")
    public List<WerknemerResponseDTO> geefAlleWerknemers() {
        return teamService.geefAlleWerknemers();
    }

    @GetMapping("/sites")
    public List<SiteResponseDTO> geefAlleSites() {
        return teamService.geefAlleSites();
    }

    @PostMapping
    public TeamResponseDTO maakTeam(@RequestBody CreateTeamRequestDTO dto) {
        return teamService.maakTeam(dto);
    }

    @PutMapping("/{teamId}/{werknemerId}")
    public List<WerknemerResponseDTO> voegToeAanTeam(@PathVariable Integer teamId, @PathVariable Integer werknemerId) {
        return teamService.voegToeAanTeam(teamId, werknemerId);
    }

    @GetMapping("/werknemer/{werknemerId}")
    public List<TeamResponseDTO> geefTeamsVanWerknemer(@PathVariable Integer werknemerId) {
        return teamService.geefTeamsVanWerknemer(werknemerId);
    }
}
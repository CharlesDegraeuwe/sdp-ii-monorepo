package hogent.sdp2.backend.controller;

import hogent.sdp2.backend.dto.request.TeamResponseDTO;
import hogent.sdp2.backend.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.service.TeamService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @GetMapping()
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
    @PutMapping("/{teamId}/{werknemerId}")
    public  List<WerknemerResponseDTO> voegToeAanTeam(@PathVariable Integer teamId, @PathVariable Integer werknemerId) {
        return teamService.voegToeAanTeam(teamId, werknemerId);
    }
}
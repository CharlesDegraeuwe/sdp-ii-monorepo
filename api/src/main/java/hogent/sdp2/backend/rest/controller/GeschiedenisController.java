package hogent.sdp2.backend.rest.controller;

import hogent.sdp2.backend.auth.SessieService;
import hogent.sdp2.backend.rest.dto.response.GeschiedenisItemDTO;
import hogent.sdp2.backend.rest.dto.response.GeschiedenisItemMetWerknemerDTO;
import hogent.sdp2.backend.rest.dto.response.TeamMetLedenDTO;
import hogent.sdp2.backend.rest.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.rest.service.afwezigheid.GeschiedenisService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/geschiedenis")
@RequiredArgsConstructor
public class GeschiedenisController {

    private final GeschiedenisService geschiedenisService;
    private final SessieService sessieService;

    @GetMapping("/werknemer/{werknemerId}")
    public List<GeschiedenisItemDTO> geefGeschiedenisVanWerknemer(
            @PathVariable Integer werknemerId) {
        sessieService.assertToegangTotWerknemer(werknemerId);
        return geschiedenisService.geefGeschiedenisVanWerknemer(werknemerId);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    @GetMapping("/team/{managerId}")
    public List<WerknemerResponseDTO> geefTeamledenVanManager(@PathVariable Integer managerId) {
        return geschiedenisService.geefTeamledenVanManager(managerId);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    @GetMapping("/teams/{managerId}")
    public List<TeamMetLedenDTO> geefTeamsVanManager(@PathVariable Integer managerId) {
        return geschiedenisService.geefTeamsVanManager(managerId);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    @GetMapping("/team-overzicht/{teamId}")
    public List<GeschiedenisItemMetWerknemerDTO> geefTeamOverzicht(@PathVariable Integer teamId) {
        return geschiedenisService.geefTeamOverzicht(teamId);
    }
}

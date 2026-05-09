package hogent.sdp2.backend.REST.controller;

import hogent.sdp2.backend.REST.dto.response.GeschiedenisItemDTO;
import hogent.sdp2.backend.REST.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.REST.service.afwezigheid.GeschiedenisService;
import hogent.sdp2.backend.auth.SessieService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/geschiedenis")
@RequiredArgsConstructor
public class GeschiedenisController {

    private final GeschiedenisService geschiedenisService;
    private final SessieService sessieService;

    @GetMapping("/werknemer/{werknemerId}")
    public List<GeschiedenisItemDTO> geefGeschiedenisVanWerknemer(@PathVariable Integer werknemerId) {
        sessieService.assertToegangTotWerknemer(werknemerId);
        return geschiedenisService.geefGeschiedenisVanWerknemer(werknemerId);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    @GetMapping("/team/{managerId}")
    public List<WerknemerResponseDTO> geefTeamledenVanManager(@PathVariable Integer managerId) {
        return geschiedenisService.geefTeamledenVanManager(managerId);
    }
}

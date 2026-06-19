package hogent.sdp2.backend.rest.controller;

import hogent.sdp2.backend.auth.SessieService;
import hogent.sdp2.backend.rest.dto.request.AfwezigheidAanmakenDTO;
import hogent.sdp2.backend.rest.dto.response.AfwezigheidsOverzichtDTO;
import hogent.sdp2.backend.rest.service.afwezigheid.AfwezigheidService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/afwezigheid")
@RequiredArgsConstructor
public class AfwezigheidController {

    private final AfwezigheidService afwezigheidService;
    private final SessieService sessieService;

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @GetMapping
    public List<AfwezigheidsOverzichtDTO> getAlleAfwezigheden() {
        return afwezigheidService.getAlleAfwezigheden();
    }

    @GetMapping("/werknemer/{werknemerId}")
    public List<AfwezigheidsOverzichtDTO> geefAfwezighedenVanWerknemer(
            @PathVariable Integer werknemerId) {
        sessieService.assertToegangTotWerknemer(werknemerId);
        return afwezigheidService.geefAfwezighedenVanWerknemer(werknemerId);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    @GetMapping("/team")
    public List<AfwezigheidsOverzichtDTO> geefAfwezighedenVanTeam() {
        return afwezigheidService.geefAfwezighedenVanTeam();
    }

    @PostMapping
    public String meldAfwezigheid(@RequestBody AfwezigheidAanmakenDTO dto) {
        return afwezigheidService.meldAfwezigheid(dto);
    }

    @GetMapping("/huidig")
    public long getHuidigeAfwezigen() {
        return afwezigheidService.telHuidigeAfwezigen();
    }
}

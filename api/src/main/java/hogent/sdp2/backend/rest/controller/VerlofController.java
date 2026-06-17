package hogent.sdp2.backend.rest.controller;

import hogent.sdp2.backend.auth.SessieService;
import hogent.sdp2.backend.rest.dto.request.VerlofAanvragenDTO;
import hogent.sdp2.backend.rest.dto.response.GeschiedenisItemDTO;
import hogent.sdp2.backend.rest.service.afwezigheid.VerlofService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verlof")
@RequiredArgsConstructor
public class VerlofController {

    private final VerlofService verlofService;
    private final SessieService sessieService;

    @PostMapping
    public String vraagVerlofAan(@RequestBody VerlofAanvragenDTO dto) {
        return verlofService.vraagVerlofAan(dto);
    }

    @GetMapping("/werknemer/{werknemerId}")
    public List<GeschiedenisItemDTO> geefVerlofVanWerknemer(@PathVariable Integer werknemerId) {
        sessieService.assertToegangTotWerknemer(werknemerId);
        return verlofService.geefVerlofVanWerknemer(werknemerId);
    }

    @GetMapping("/{verlofId}/status")
    public String geefVerlofStatus(@PathVariable Integer verlofId) {
        return verlofService.geefVerlofStatus(verlofId);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    @PutMapping("/{verlofId}/goedkeuren")
    public String keurVerlofGoed(@PathVariable Integer verlofId) {
        verlofService.assertMagVerlofBeheren(verlofId);
        return verlofService.keurVerlofGoed(verlofId);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    @PutMapping("/{verlofId}/afwijzen")
    public String wijsVerlofAf(@PathVariable Integer verlofId) {
        verlofService.assertMagVerlofBeheren(verlofId);
        return verlofService.wijsVerlofAf(verlofId);
    }

    @PutMapping("/{verlofId}/annuleren")
    public String annuleerVerlof(@PathVariable Integer verlofId) {
        return verlofService.annuleerVerlof(verlofId);
    }
}

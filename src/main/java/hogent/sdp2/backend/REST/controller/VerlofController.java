package hogent.sdp2.backend.REST.controller;

import hogent.sdp2.backend.REST.dto.request.VerlofAanvragenDTO;
import hogent.sdp2.backend.REST.service.afwezigheid.VerlofService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verlof")
@RequiredArgsConstructor
public class VerlofController {

    private final VerlofService verlofService;

    @PostMapping
    public String vraagVerlofAan(@RequestBody VerlofAanvragenDTO dto) {
        return verlofService.vraagVerlofAan(dto);
    }

    @GetMapping("/{verlofId}/status")
    public String geefVerlofStatus(@PathVariable Integer verlofId) {
        return verlofService.geefVerlofStatus(verlofId);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    @PutMapping("/{verlofId}/goedkeuren")
    public String keurVerlofGoed(@PathVariable Integer verlofId) {
        return verlofService.keurVerlofGoed(verlofId);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    @PutMapping("/{verlofId}/afwijzen")
    public String wijsVerlofAf(@PathVariable Integer verlofId) {
        return verlofService.wijsVerlofAf(verlofId);
    }

    @PutMapping("/{verlofId}/annuleren")
    public String annuleerVerlof(@PathVariable Integer verlofId) {
        return verlofService.annuleerVerlof(verlofId);
    }
}

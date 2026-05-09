package hogent.sdp2.backend.REST.controller;

import hogent.sdp2.backend.REST.dto.response.AfwezigheidsOverzichtDTO;
import hogent.sdp2.backend.REST.service.planning.PlanningService;
import hogent.sdp2.backend.auth.SessieService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/planning")
@RequiredArgsConstructor
public class PlanningController {

    private final PlanningService planningService;
    private final SessieService sessieService;

    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    @GetMapping("/team/{werknemerId}")
    public List<AfwezigheidsOverzichtDTO> geefAfwezighedenVanTeam(
            @PathVariable Integer werknemerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate van,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tot
    ) {
        return planningService.geefAfwezighedenVanTeam(werknemerId, van, tot);
    }
}

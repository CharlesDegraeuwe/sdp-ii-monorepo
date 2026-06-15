package hogent.sdp2.backend.rest.controller;

import hogent.sdp2.backend.rest.dto.response.AfwezigheidsOverzichtDTO;
import hogent.sdp2.backend.rest.service.planning.PlanningService;
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
        sessieService.assertToegangTotWerknemer(werknemerId);
        return planningService.geefAfwezighedenVanTeam(werknemerId, van, tot);
    }

    @GetMapping("/werknemer/{werknemerId}")
    public List<AfwezigheidsOverzichtDTO> geefPlanningVanWerknemer(
            @PathVariable Integer werknemerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate van,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tot
    ) {
        sessieService.assertToegangTotWerknemer(werknemerId);
        return planningService.geefPlanningVanWerknemer(werknemerId, van, tot);
    }
}

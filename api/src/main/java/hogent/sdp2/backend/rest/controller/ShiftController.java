package hogent.sdp2.backend.rest.controller;

import hogent.sdp2.backend.rest.dto.request.ShiftAanmakenDTO;
import hogent.sdp2.backend.rest.dto.request.ShiftAanpassenDTO;
import hogent.sdp2.backend.rest.dto.response.ShiftResponseDTO;
import hogent.sdp2.backend.rest.service.planning.ShiftService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    @GetMapping("/team/{teamId}")
    public List<ShiftResponseDTO> geefShiftenVanTeam(
            @PathVariable Integer teamId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datum) {
        return shiftService.geefShiftenVanTeamOpDatum(teamId, datum);
    }

    @GetMapping("/werknemer/{werknemerId}")
    public List<ShiftResponseDTO> geefShiftenVanWerknemer(
            @PathVariable Integer werknemerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datum) {
        return shiftService.geefShiftenVanWerknemerOpDatum(werknemerId, datum);
    }

    // ZET DEZE TIJDELIJK IN COMMENTAAR OM TE TESTEN
    // @PreAuthorize("hasAnyAuthority('Admin', 'Manager', 'ADMIN', 'MANAGER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @PostMapping
    public ShiftResponseDTO maakShift(@RequestBody ShiftAanmakenDTO dto) {

        // --- HARDCORE DEBUG LIJNEN ---
        System.out.println("==== DEBUG BACKEND SHIFT AANMAKEN ====");
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            System.out.println("Ingelogde User: " + auth.getName());
            System.out.println("Toegekende Rechten (Authorities): " + auth.getAuthorities());
        } else {
            System.out.println("LET OP: Geen authenticatie gevonden in de SecurityContext!");
        }
        System.out.println("Ontvangen DTO: " + dto);
        System.out.println("======================================");
        // -----------------------------

        return shiftService.maakShift(dto);
    }

    @PreAuthorize("hasAnyAuthority('Admin', 'Manager')")
    @PutMapping("/{shiftId}")
    public ShiftResponseDTO pasAan(
        @PathVariable Integer shiftId, @RequestBody ShiftAanpassenDTO dto) {
        return shiftService.pasAan(shiftId, dto);
    }
}

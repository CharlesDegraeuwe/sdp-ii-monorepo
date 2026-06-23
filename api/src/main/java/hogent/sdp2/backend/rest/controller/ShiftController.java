package hogent.sdp2.backend.rest.controller;

import hogent.sdp2.backend.rest.dto.request.ShiftAanmakenDTO;
import hogent.sdp2.backend.rest.dto.request.ShiftAanpassenDTO;
import hogent.sdp2.backend.rest.dto.response.ShiftResponseDTO;
import hogent.sdp2.backend.rest.service.planning.ShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    @GetMapping("/team/{teamId}")
    public List<ShiftResponseDTO> geefShiftenVanTeam(
            @PathVariable Integer teamId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datum
    ) {
        return shiftService.geefShiftenVanTeamOpDatum(teamId, datum);
    }

    @GetMapping("/werknemer/{werknemerId}")
    public List<ShiftResponseDTO> geefShiftenVanWerknemer(
            @PathVariable Integer werknemerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datum
    ) {
        return shiftService.geefShiftenVanWerknemerOpDatum(werknemerId, datum);
    }

    @GetMapping("/werknemer/{werknemerId}/bereik")
    public List<ShiftResponseDTO> geefShiftenVanWerknemerInBereik(
            @PathVariable Integer werknemerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate van,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tot
    ) {
        return shiftService.geefShiftenVanWerknemerInBereik(werknemerId, van, tot);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    @PostMapping
    public ShiftResponseDTO maakShift(@RequestBody ShiftAanmakenDTO dto) {
        return shiftService.maakShift(dto);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    @PutMapping("/{shiftId}")
    public ShiftResponseDTO pasAan(
            @PathVariable Integer shiftId,
            @RequestBody ShiftAanpassenDTO dto
    ) {
        return shiftService.pasAan(shiftId, dto);
    }
}

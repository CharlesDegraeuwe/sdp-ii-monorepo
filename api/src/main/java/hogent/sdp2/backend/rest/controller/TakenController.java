package hogent.sdp2.backend.rest.controller;

import hogent.sdp2.backend.auth.SessieService;
import hogent.sdp2.backend.rest.dto.request.TaakAanmakenDTO;
import hogent.sdp2.backend.rest.dto.request.TaakResponseDTO;
import hogent.sdp2.backend.rest.service.taken.TakenService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/taken")
@RequiredArgsConstructor
public class TakenController {

    private final TakenService takenService;
    private final SessieService sessieService;

    @GetMapping("/werknemer/{id}")
    public List<TaakResponseDTO> geefTakenVanWerknemer(@PathVariable int id) {
        sessieService.assertToegangTotWerknemer(id);
        return takenService.geefTakenVanWerknemer(id);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @PostMapping
    public String maakTaakAan(@RequestBody TaakAanmakenDTO dto) {
        return takenService.maakTaakAan(dto);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @GetMapping("/alle")
    public List<TaakResponseDTO> geefAlleTaken() {
        return takenService.geefAlleTaken();
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @PutMapping("/{id}/toewijzen")
    public String wijsTaakToe(@PathVariable int id, @RequestParam int werknemerId) {
        return takenService.wijsTaakToe(id, werknemerId);
    }

    @PutMapping("/{id}/afgewerkt")
    public String markeerAfgewerkt(@PathVariable int id) {
        takenService.assertEigenaarVanTaak(id, sessieService);
        return takenService.markeerAfgewerkt(id);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> verwijderTaak(@PathVariable int id) {
        return ResponseEntity.ok(takenService.verwijderTaak(id));
    }

    @PutMapping("/{taakId}/toewijzingen")
    public ResponseEntity<String> updateToewijzingen(
            @PathVariable Integer taakId, @RequestBody List<Integer> werknemerIds) {
        try {
            takenService.updateToewijzingen(taakId, werknemerIds);
            return ResponseEntity.ok("Toewijzingen succesvol bijgewerkt");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fout bij updaten: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @PutMapping("/{id}/inplannen")
    public ResponseEntity<String> planTaakIn(
            @PathVariable int id,
            @RequestParam String datum,
            @RequestParam String startuur,
            @RequestParam String einduur) {
        try {
            takenService.planTaakIn(id, datum, startuur, einduur);
            return ResponseEntity.ok("Taak succesvol ingepland!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fout: " + e.getMessage());
        }
    }
}

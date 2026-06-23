package hogent.sdp2.backend.rest.controller;

import hogent.sdp2.backend.rest.dto.request.TaakAanmakenDTO;
import hogent.sdp2.backend.rest.dto.request.TaakResponseDTO;
import hogent.sdp2.backend.rest.service.taken.TakenService;
import hogent.sdp2.backend.auth.SessieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
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
}

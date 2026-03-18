package hogent.sdp2.backend.controller;

import hogent.sdp2.backend.dto.request.TaakAanmakenDTO;
import hogent.sdp2.backend.dto.request.TaakResponseDTO;
import hogent.sdp2.backend.service.TakenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taken")
@RequiredArgsConstructor
public class TakenController {

    private final TakenService takenService;

    @GetMapping("/werknemer/{id}")
    public List<TaakResponseDTO> geefTakenVanWerknemer(@PathVariable int id) {
        return takenService.geefTakenVanWerknemer(id);
    }

    @PostMapping
    public String maakTaakAan(@RequestBody TaakAanmakenDTO dto) {
        return takenService.maakTaakAan(dto);
    }

    @GetMapping("/alle")
    public List<TaakResponseDTO> geefAlleTaken() {
        return takenService.geefAlleTaken();
    }

    @PutMapping("/{id}/toewijzen")
    public String wijsTaakToe(@PathVariable int id, @RequestParam int werknemerId) {
        return takenService.wijsTaakToe(id, werknemerId);
    }

    @PutMapping("/{id}/afgewerkt")
    public String markeerAfgewerkt(@PathVariable int id) {
        return takenService.markeerAfgewerkt(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> verwijderTaak(@PathVariable int id) {
        return ResponseEntity.ok(takenService.verwijderTaak(id));
    }
}
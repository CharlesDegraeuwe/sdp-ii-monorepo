package hogent.sdp2.backend.REST.controller;

import hogent.sdp2.backend.REST.dto.request.*;
import hogent.sdp2.backend.REST.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.REST.service.werknemer.WerknemerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/werknemers")
@RequiredArgsConstructor
public class WerknemerController {

    private final WerknemerService werknemerService;


    @PostMapping("/login-mail")
    public ResponseEntity<Void> loginMail(@RequestBody EmailLoginDTO dto) {
        werknemerService.emailLogin(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login-token")
    public ResponseEntity<LoginResponseDTO> loginToken(@RequestBody TokenLoginDto dto) {
        return ResponseEntity.ok(werknemerService.tokenLogin(dto));
    }

    @PostMapping("/activeer")
    public ResponseEntity<String> activeerWerknemer(@RequestParam String code) {
        return ResponseEntity.ok(werknemerService.activeerAccount(code));
    }


    @PutMapping("/wachtwoord")
    public ResponseEntity<String> wijzigWachtwoord(@RequestBody WachtwoordWijzigenDTO dto) {
        return ResponseEntity.ok(werknemerService.wijzigWachtwoord(dto));
    }

    @PostMapping("/wachtwoord-vergeten")
    public ResponseEntity<Void> wachtwoordVergeten(@RequestBody WachtwoordVergetenDTO dto) {
        werknemerService.wachtwoordVergetenAanvragen(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/wachtwoord-resetten")
    public ResponseEntity<String> resetWachtwoord(@RequestBody WachtwoordResetDTO dto) {
        return ResponseEntity.ok(werknemerService.resetWachtwoord(dto));
    }


    @PostMapping
    public ResponseEntity<String> createWerknemer(@RequestBody WerknemerAanmakenDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(werknemerService.maakWerknemer(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WerknemerResponseDTO> update(@PathVariable Integer id, @RequestBody UpdateUserDTO dto) {
        return ResponseEntity.ok(werknemerService.updateUser(dto));
    }

    @GetMapping()
    public ResponseEntity<List<WerknemerResponseDTO>> getAlleUsers() {
        return ResponseEntity.ok(werknemerService.getAlleUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WerknemerResponseDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(werknemerService.getByID(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<WerknemerResponseDTO> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(werknemerService.getByEmail(email));
    }
}
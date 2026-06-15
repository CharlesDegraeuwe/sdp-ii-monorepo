package hogent.sdp2.backend.rest.controller;

import hogent.sdp2.backend.rest.dto.request.*;
import hogent.sdp2.backend.rest.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.rest.service.werknemer.WerknemerService;
import hogent.sdp2.backend.auth.SessieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/werknemers")
@RequiredArgsConstructor
public class WerknemerController {

    private final WerknemerService werknemerService;
    private final SessieService sessieService;


    @PostMapping("/login-mail")
    public ResponseEntity<Void> loginMail(@RequestBody EmailLoginDTO dto) {
        werknemerService.emailLogin(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login-token")
    public ResponseEntity<LoginResponseDTO> loginToken(@RequestBody TokenLoginDto dto) {
        return ResponseEntity.ok(werknemerService.tokenLogin(dto));
    }

    @PostMapping("/login-password")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        try {
            return ResponseEntity.ok(werknemerService.passwordLogin(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
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
    public ResponseEntity<?> wachtwoordVergeten(@RequestBody WachtwoordVergetenDTO dto) {
        try {
            werknemerService.wachtwoordVergetenAanvragen(dto);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/wachtwoord-resetten")
    public ResponseEntity<?> resetWachtwoord(@RequestBody WachtwoordResetDTO dto) {
        try {
            return ResponseEntity.ok(werknemerService.resetWachtwoord(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @PostMapping
    public ResponseEntity<String> createWerknemer(@RequestBody WerknemerAanmakenDTO dto) {
        // Alleen admins mogen Admin/Manager rollen aanmaken
        if (("Admin".equals(dto.rol()) || "Manager".equals(dto.rol())) && !sessieService.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Alleen admins mogen admins of managers aanmaken");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(werknemerService.maakWerknemer(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WerknemerResponseDTO> update(@PathVariable Integer id, @RequestBody UpdateUserDTO dto) {
        // Admin/Manager mag iedereen updaten, anders alleen jezelf
        if (!sessieService.isAdminOfManager() && !sessieService.getIngelogdeWerknemerId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(werknemerService.updateUser(dto));
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @GetMapping()
    public ResponseEntity<List<WerknemerResponseDTO>> getAlleUsers() {
        return ResponseEntity.ok(werknemerService.getAlleUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WerknemerResponseDTO> getById(@PathVariable Integer id) {
        sessieService.assertToegangTotWerknemer(id);
        return ResponseEntity.ok(werknemerService.getByID(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<WerknemerResponseDTO> getByEmail(@PathVariable String email) {
        // Admin/Manager mag iedereen opzoeken, anders alleen jezelf
        if (!sessieService.isAdminOfManager()) {
            String ingelogdeEmail = sessieService.getIngelogdeWerknemer().getEmail();
            if (!ingelogdeEmail.equals(email)) {
                sessieService.assertAdminOfManager(); // gooit AccessDeniedException
            }
        }
        return ResponseEntity.ok(werknemerService.getByEmail(email));
    }
}

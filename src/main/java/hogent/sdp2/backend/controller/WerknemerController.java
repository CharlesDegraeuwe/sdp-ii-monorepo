package hogent.sdp2.backend.controller;

import hogent.sdp2.backend.dto.request.*;
import hogent.sdp2.backend.service.WerknemerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/werknemers")
@RequiredArgsConstructor
public class WerknemerController {

    private final WerknemerService werknemerService;

    @PostMapping
    public String createWerknemer(@RequestBody WerknemerAanmakenDTO dto) {
        return werknemerService.maakWerknemer(dto);
    }

    @PostMapping("/activeer")
    public String activeerWerknemer(@RequestParam String code) {
        return werknemerService.activeerAccount(code);
    }

    @PostMapping("/login")
    public WerknemerResponseDTO login(@RequestBody LoginRequestDTO dto) {
        return werknemerService.login(dto);
    }

    @PutMapping("/wachtwoord")
    public String wijzigWachtwoord(@RequestBody WachtwoordWijzigenDTO dto) {
        return werknemerService.wijzigWachtwoord(dto);
    }

    @PostMapping("/wachtwoord-vergeten")
    public String wachtwoordVergeten(@RequestBody WachtwoordVergetenDTO dto) {
        return werknemerService.wachtwoordVergetenAanvragen(dto);
    }

    @PostMapping("/wachtwoord-resetten")
    public String resetWachtwoord(@RequestBody WachtwoordResetDTO dto) {
        return werknemerService.resetWachtwoord(dto);
    }


    @GetMapping("/users")
    public List<WerknemerResponseDTO> getAlleUsers() {
        return werknemerService.getAlleUsers();
    }
}


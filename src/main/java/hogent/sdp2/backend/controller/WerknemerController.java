package hogent.sdp2.backend.controller;

import hogent.sdp2.backend.dto.request.*;
import hogent.sdp2.backend.dto.response.WerknemerResponseDTO;
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
    public LoginResponseDTO login(@RequestBody LoginRequestDTO dto) {
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

    @PutMapping("/update")
    public WerknemerResponseDTO update(@RequestBody UpdateUserDTO dto) {return werknemerService.updateUser(dto);}

    @GetMapping("/users")
    public List<WerknemerResponseDTO> getAlleUsers() {
        return werknemerService.getAlleUsers();
    }

    @GetMapping("/email")
    public WerknemerResponseDTO getByEmail(@RequestParam String email) {
        return werknemerService.getByEmail(email);
    }

    @GetMapping("/byid")
    public WerknemerResponseDTO getById(@RequestParam Integer id) {
        return werknemerService.getByID(id);
    }


    //TODO
    // nog toe te voegen:
    // 1. Activeren van activeren, deactiveren, blokkeren, deblokkeren voor admins (zonder code dus)
    // 2. aanmaken en returnen JWT Tokens toevoegen bij inloggen
    // 3. Verficiatie pipeline van de data
    // 4. Authenticatie en authorisatie pipeline zodat niet iedereen alle endpoints kan raadplegen
    // komt goed
}


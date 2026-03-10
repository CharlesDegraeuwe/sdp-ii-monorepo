package hogent.sdp2.backend.controller;

import hogent.sdp2.backend.dto.request.*;
import hogent.sdp2.backend.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.service.WerknemerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//TODO validatie oplossen via builder variant en dan als één exception teruggeven (map<>)
@RestController
@RequestMapping("/api/werknemers")
@RequiredArgsConstructor
public class WerknemerController {

    private final WerknemerService werknemerService;

    @PostMapping
    public String createWerknemer(@RequestBody WerknemerAanmakenDTO dto) {
        return werknemerService.maakWerknemer(dto);
    }

    @PostMapping("/{id}/activeer")
    public String activeerWerknemer(@PathVariable int id, @RequestParam String code) {
        return werknemerService.activeerAccount(id, code);
    }

    @PostMapping("/login")
    public
    ResponseEntity<WerknemerResponseDTO>
    login(@RequestBody
          LoginRequestDTO dto,
          HttpServletRequest request
    ) {
        WerknemerResponseDTO werknemer = werknemerService.login(dto);
        HttpSession session = request.getSession();
        session.setAttribute("gebruiker", werknemer);
        return ResponseEntity.ok(werknemer);
    }

    @PutMapping("/wachtwoord")
    public String wijzigWachtwoord(
            @RequestBody WachtwoordWijzigenDTO dto
    ) {
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

    @PutMapping("/{id}/blokkeer")
    public String blokkeerWerknemerAdmin(@PathVariable Integer id) {
        return werknemerService.blokkeerWerknemerAdmin(id);
    }

    @PutMapping("/{id}/activeer")
    public String activeerWerknemerAdmin(@PathVariable Integer id) {
        return werknemerService.activeerWerknemerAdmin(id);
    }

    @PutMapping("/{id}/deactiveer")
    public String deactiveerWerknemerAdmin(@PathVariable Integer id) {
        return werknemerService.deactiveerWerknemerAdmin(id);
    }

    @DeleteMapping("/{id}")
    public String deleteWerknemer(@PathVariable Integer id) {
        return werknemerService.verwijderWerknemer(id);
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
    // 2. Verficiatie pipeline van de data
    // 3. Authenticatie en authorisatie pipeline zodat niet iedereen alle endpoints kan raadplegen
    // komt goed
}


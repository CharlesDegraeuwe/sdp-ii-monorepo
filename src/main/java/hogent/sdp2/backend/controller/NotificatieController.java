package hogent.sdp2.backend.controller;

import hogent.sdp2.backend.dto.request.NotificatieDTO;
import hogent.sdp2.backend.service.NotificatieService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaties")
@RequiredArgsConstructor
public class NotificatieController {

    private final NotificatieService notificatieService;

    @GetMapping("/{werknemerId}")
    public List<NotificatieDTO> geefNotificaties(@PathVariable Integer werknemerId) {
        return notificatieService.geefNotificatiesVanWerknemer(werknemerId);
    }

    @GetMapping("/{werknemerId}/ongelezen")
    public long geefAantalOngelezen(@PathVariable Integer werknemerId) {
        return notificatieService.geefAantalOngelezenNotificaties(werknemerId);
    }

    @PutMapping("/{notificatieId}/gelezen")
    public String markeerAlsGelezen(@PathVariable Integer notificatieId) {
        return notificatieService.markeerAlsGelezen(notificatieId);
    }

    @DeleteMapping("/{notificatieId}")
    public String verwijderNotificatie(@PathVariable Integer notificatieId) {
        return notificatieService.verwijderNotificatie(notificatieId);
    }
}
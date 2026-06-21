package hogent.sdp2.backend.rest.service.notificatie;

import hogent.sdp2.backend.domain.Notificatie;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.rest.dto.request.NotificatieDTO;
import hogent.sdp2.backend.rest.repository.NotificatieRepository;
import hogent.sdp2.backend.rest.repository.WerknemerRepository;
import hogent.sdp2.backend.rest.service.sse.SseService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificatieService {

    private final NotificatieRepository notificatieRepository;
    private final WerknemerRepository werknemerRepository;
    private final SseService sseService;

    public void maakNotificatie(Integer werknemerId, String titel, String bericht) {
        maakNotificatie(werknemerId, titel, bericht, null);
    }

    public void maakNotificatie(
            Integer werknemerId, String titel, String bericht, Integer referentieId) {
        Werknemer werknemer =
                werknemerRepository
                        .findById(werknemerId)
                        .orElseThrow(() -> new RuntimeException("Werknemer niet gevonden"));

        Notificatie notificatie = new Notificatie();
        notificatie.setWerknemer(werknemer);
        notificatie.setTitel(titel);
        notificatie.setBericht(bericht);
        notificatie.setGelezen("Nee");
        notificatie.setDatum(LocalDate.now());
        notificatie.setReferentieId(referentieId);

        notificatieRepository.save(notificatie);

        sseService.pushEvent(
                werknemerId,
                "nieuwe_notificatie",
                new NotificatieDTO(
                        notificatie.getId(),
                        werknemerId,
                        notificatie.getTitel(),
                        notificatie.getBericht(),
                        notificatie.getGelezen(),
                        notificatie.getDatum(),
                        notificatie.getReferentieId()));
    }

    public List<NotificatieDTO> geefNotificatiesVanWerknemer(Integer werknemerId) {
        return notificatieRepository.findByWerknemerIdOrderByDatumDesc(werknemerId).stream()
                .map(n ->
                                new NotificatieDTO(
                                        n.getId(),
                                        n.getWerknemer().getId(),
                                        n.getTitel(),
                                        n.getBericht(),
                                        n.getGelezen(),
                                        n.getDatum(),
                                        n.getReferentieId()))
                .toList();
    }

    public long geefAantalOngelezenNotificaties(Integer werknemerId) {
        return notificatieRepository.countByWerknemerIdAndGelezen(werknemerId, "Nee");
    }

    public String markeerAlsGelezen(Integer notificatieId) {
        Notificatie notificatie =
                notificatieRepository
                        .findById(notificatieId)
                        .orElseThrow(() -> new RuntimeException("Notificatie niet gevonden"));
        notificatie.setGelezen("Ja");
        notificatieRepository.save(notificatie);
        return "Notificatie gemarkeerd als gelezen.";
    }

    public String verwijderNotificatie(Integer notificatieId) {
        notificatieRepository.deleteById(notificatieId);
        return "Notificatie verwijderd.";
    }

    public boolean bestaatAl(Integer werknemerId, String titel, Integer referentieId) {
        return notificatieRepository.existsByWerknemerIdAndTitelAndReferentieId(werknemerId, titel, referentieId);
    }
}

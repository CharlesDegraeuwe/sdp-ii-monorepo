package hogent.sdp2.backend.service;

import hogent.sdp2.backend.domain.Notificaty;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.dto.request.NotificatieDTO;
import hogent.sdp2.backend.repository.NotificatieRepository;
import hogent.sdp2.backend.repository.WerknemerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificatieService {

    private final NotificatieRepository notificatieRepository;
    private final WerknemerRepository werknemerRepository;

    public void maakNotificatie(Integer werknemerId, String titel, String bericht) {
        maakNotificatie(werknemerId, titel, bericht, null);
    }

    public void maakNotificatie(Integer werknemerId, String titel, String bericht, Integer referentieId) {
        Werknemer werknemer = werknemerRepository.findById(werknemerId)
                .orElseThrow(() -> new RuntimeException("Werknemer niet gevonden"));

        Notificaty notificatie = new Notificaty();
        notificatie.setWerknemer(werknemer);
        notificatie.setTitel(titel);
        notificatie.setBericht(bericht);
        notificatie.setGelezen("Nee");
        notificatie.setDatum(LocalDate.now());
        notificatie.setReferentieId(referentieId);

        notificatieRepository.save(notificatie);
    }

    public List<NotificatieDTO> geefNotificatiesVanWerknemer(Integer werknemerId) {
        return notificatieRepository.findByWerknemerIdOrderByDatumDesc(werknemerId)
                .stream()
                .map(n -> new NotificatieDTO(
                        n.getId(),
                        n.getWerknemer().getId(),
                        n.getTitel(),
                        n.getBericht(),
                        n.getGelezen(),
                        n.getDatum(),
                        n.getReferentieId()
                ))
                .toList();
    }

    public long geefAantalOngelezenNotificaties(Integer werknemerId) {
        return notificatieRepository.countByWerknemerIdAndGelezen(werknemerId, "Nee");
    }

    public String markeerAlsGelezen(Integer notificatieId) {
        Notificaty notificatie = notificatieRepository.findById(notificatieId)
                .orElseThrow(() -> new RuntimeException("Notificatie niet gevonden"));
        notificatie.setGelezen("Ja");
        notificatieRepository.save(notificatie);
        return "Notificatie gemarkeerd als gelezen.";
    }

    public String verwijderNotificatie(Integer notificatieId) {
        notificatieRepository.deleteById(notificatieId);
        return "Notificatie verwijderd.";
    }
}
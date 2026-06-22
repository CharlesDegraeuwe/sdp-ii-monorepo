package hogent.sdp2.backend.rest.service.afwezigheid;

import hogent.sdp2.backend.auth.SessieService;
import hogent.sdp2.backend.domain.Teamwerknemer;
import hogent.sdp2.backend.domain.Verlof;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.rest.dto.request.VerlofAanvragenDTO;
import hogent.sdp2.backend.rest.dto.response.GeschiedenisItemDTO;
import hogent.sdp2.backend.rest.repository.TeamwerknemerRepository;
import hogent.sdp2.backend.rest.repository.VerlofRepository;
import hogent.sdp2.backend.rest.repository.WerknemerRepository;
import hogent.sdp2.backend.rest.service.notificatie.NotificatieService;
import hogent.sdp2.backend.rest.service.sse.SseService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VerlofService {

    private final VerlofRepository verlofRepository;
    private final WerknemerRepository werknemerRepository;
    private final TeamwerknemerRepository teamwerknemerRepository;
    private final NotificatieService notificatieService;
    private final SessieService sessieService;
    private final SseService sseService;

    @Transactional
    public String vraagVerlofAan(VerlofAanvragenDTO dto) {
        Werknemer werknemer =
                werknemerRepository
                        .findById(dto.werknemerId())
                        .orElseThrow(() -> new RuntimeException("Werknemer niet gevonden"));

        if (dto.eindDatum().isBefore(dto.startDatum())) {
            throw new RuntimeException("Einddatum mag niet voor startdatum liggen.");
        }

        List<Teamwerknemer> memberships =
                teamwerknemerRepository.findByWerknemerId(dto.werknemerId());
        if (memberships.isEmpty()) {
            throw new RuntimeException("Geen manager gevonden voor deze werknemer.");
        }

        List<Werknemer> managers =
                teamwerknemerRepository
                        .findGoedkeurderVanTeam(memberships.get(0).getTeam().getId())
                        .stream()
                        .map(Teamwerknemer::getWerknemer)
                        .toList();

        if (managers.isEmpty()) {
            throw new RuntimeException("Geen manager gevonden voor deze werknemer.");
        }

        Integer goedkeurderId = managers.get(0).getId();

        Verlof verlof = new Verlof();
        verlof.setWerknemer(werknemer);
        verlof.setStartDatum(dto.startDatum());
        verlof.setEindDatum(dto.eindDatum());
        verlof.setType(dto.type());
        verlof.setStatus("In afwachting");
        verlof.setGoedkeurderId(goedkeurderId);

        verlofRepository.save(verlof);

        String bericht =
                werknemer.getVoornaam()
                        + " "
                        + werknemer.getNaam()
                        + " heeft verlof aangevraagd van "
                        + dto.startDatum()
                        + " tot "
                        + dto.eindDatum()
                        + ".";

        for (Werknemer manager : managers) {
            notificatieService.maakNotificatie(
                    manager.getId(), "Nieuwe verlofaanvraag", bericht, verlof.getId());
            sseService.pushEvent(
                    manager.getId(),
                    "verlof_aangevraagd",
                    Map.of("verlofId", verlof.getId(), "werknemerId", dto.werknemerId()));
        }

        return "Verlofaanvraag succesvol ingediend.";
    }

    public String geefVerlofStatus(Integer verlofId) {
        return verlofRepository
                .findById(verlofId)
                .map(Verlof::getStatus)
                .orElseThrow(() -> new RuntimeException("Verlof niet gevonden"));
    }

    public String keurVerlofGoed(Integer verlofId) {
        Verlof verlof =
                verlofRepository
                        .findById(verlofId)
                        .orElseThrow(() -> new RuntimeException("Verlof niet gevonden"));

        verlof.setStatus("Goedgekeurd");
        verlofRepository.save(verlof);

        notificatieService.maakNotificatie(
                verlof.getWerknemer().getId(),
                "Verlof goedgekeurd",
                "Jouw verlofaanvraag van "
                        + verlof.getStartDatum()
                        + " tot "
                        + verlof.getEindDatum()
                        + " is goedgekeurd.",
                verlof.getId());

        sseService.pushEvent(
                verlof.getWerknemer().getId(), "verlof_goedgekeurd", Map.of("verlofId", verlofId));

        return "Verlof goedgekeurd.";
    }

    public String wijsVerlofAf(Integer verlofId) {
        Verlof verlof =
                verlofRepository
                        .findById(verlofId)
                        .orElseThrow(() -> new RuntimeException("Verlof niet gevonden"));

        verlof.setStatus("Afgewezen");
        verlofRepository.save(verlof);

        notificatieService.maakNotificatie(
                verlof.getWerknemer().getId(),
                "Verlof afgewezen",
                "Jouw verlofaanvraag van "
                        + verlof.getStartDatum()
                        + " tot "
                        + verlof.getEindDatum()
                        + " is afgewezen.");

        sseService.pushEvent(
                verlof.getWerknemer().getId(), "verlof_afgewezen", Map.of("verlofId", verlofId));

        return "Verlof afgewezen.";
    }

    public String annuleerVerlof(Integer verlofId) {
        Verlof verlof =
                verlofRepository
                        .findById(verlofId)
                        .orElseThrow(() -> new RuntimeException("Verlof niet gevonden"));

        verlof.setStatus("Geannuleerd");
        verlofRepository.save(verlof);

        Werknemer werknemer = verlof.getWerknemer();

        String annuleerBericht =
                werknemer.getVoornaam()
                        + " "
                        + werknemer.getNaam()
                        + " heeft zijn verlof van "
                        + verlof.getStartDatum()
                        + " tot "
                        + verlof.getEindDatum()
                        + " geannuleerd.";

        teamwerknemerRepository.findByWerknemerId(werknemer.getId()).stream()
                .findFirst()
                .ifPresent(
                        tw -> {
                            teamwerknemerRepository
                                    .findByTeamId(tw.getTeam().getId())
                                    .forEach(
                                            teamlid -> {
                                                if (!teamlid.getWerknemer()
                                                        .getId()
                                                        .equals(werknemer.getId())) {
                                                    notificatieService.maakNotificatie(
                                                            teamlid.getWerknemer().getId(),
                                                            "Verlof geannuleerd",
                                                            annuleerBericht);
                                                    sseService.pushEvent(
                                                            teamlid.getWerknemer().getId(),
                                                            "verlof_geannuleerd",
                                                            Map.of(
                                                                    "verlofId",
                                                                    verlofId,
                                                                    "werknemerId",
                                                                    werknemer.getId()));
                                                }
                                            });

                            teamwerknemerRepository
                                    .findGoedkeurderVanTeam(tw.getTeam().getId())
                                    .stream()
                                    .map(Teamwerknemer::getWerknemer)
                                    .forEach(
                                            manager -> {
                                                notificatieService.maakNotificatie(
                                                        manager.getId(),
                                                        "Verlof geannuleerd",
                                                        annuleerBericht,
                                                        verlofId);
                                                sseService.pushEvent(
                                                        manager.getId(),
                                                        "verlof_geannuleerd",
                                                        Map.of(
                                                                "verlofId",
                                                                verlofId,
                                                                "werknemerId",
                                                                werknemer.getId()));
                                            });
                        });

        sseService.pushEvent(werknemer.getId(), "verlof_geannuleerd", Map.of("verlofId", verlofId));

        return "Verlof geannuleerd.";
    }

    public List<GeschiedenisItemDTO> geefVerlofVanWerknemer(Integer werknemerId) {
        return verlofRepository.findByWerknemerId(werknemerId).stream()
                .map(
                        v ->
                                new GeschiedenisItemDTO(
                                        v.getId(),
                                        "Verlof",
                                        v.getStartDatum(),
                                        v.getEindDatum(),
                                        v.getStatus(),
                                        v.getType()))
                .toList();
    }

    public void assertMagVerlofBeheren(Integer verlofId) {
        Verlof verlof =
                verlofRepository
                        .findById(verlofId)
                        .orElseThrow(() -> new RuntimeException("Verlof niet gevonden"));
        sessieService.assertMagGoedkeuren(verlof.getWerknemer().getId());
    }
}

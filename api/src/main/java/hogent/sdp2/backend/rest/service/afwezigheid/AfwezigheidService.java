package hogent.sdp2.backend.rest.service.afwezigheid;

import hogent.sdp2.backend.auth.SessieService;
import hogent.sdp2.backend.domain.Afwezigheid;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.rest.dto.request.AfwezigheidAanmakenDTO;
import hogent.sdp2.backend.rest.dto.response.AfwezigheidsOverzichtDTO;
import hogent.sdp2.backend.rest.repository.AfwezigheidRepository;
import hogent.sdp2.backend.rest.repository.TeamRepository;
import hogent.sdp2.backend.rest.repository.TeamwerknemerRepository;
import hogent.sdp2.backend.rest.repository.WerknemerRepository;
import hogent.sdp2.backend.rest.service.notificatie.NotificatieService;
import hogent.sdp2.backend.rest.service.sse.SseService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AfwezigheidService {

    private final AfwezigheidRepository afwezigheidRepository;
    private final WerknemerRepository werknemerRepository;
    private final TeamwerknemerRepository teamwerknemerRepository;
    private final TeamRepository teamRepository;
    private final NotificatieService notificatieService;
    private final SessieService sessieService;
    private final SseService sseService;

    public String meldAfwezigheid(AfwezigheidAanmakenDTO dto) {
        Werknemer werknemer =
                werknemerRepository
                        .findById(dto.werknemerId())
                        .orElseThrow(() -> new RuntimeException("Werknemer niet gevonden"));

        if (dto.eindDatum().isBefore(dto.startDatum())) {
            throw new RuntimeException("Einddatum mag niet voor startdatum liggen.");
        }

        Afwezigheid afwezigheid = new Afwezigheid();
        afwezigheid.setWerknemer(werknemer);
        afwezigheid.setStartDatum(dto.startDatum());
        afwezigheid.setEindDatum(dto.eindDatum());
        afwezigheid.setReden(dto.reden());
        afwezigheid.setCertificaat(dto.certificaat());

        afwezigheidRepository.save(afwezigheid);

        String afwezigheidBericht = werknemer.getVoornaam() + " " + werknemer.getNaam()
                + " is ziek van " + dto.startDatum() + " tot " + dto.eindDatum() + ".";

        teamwerknemerRepository.findByWerknemerId(dto.werknemerId()).stream()
                .findFirst()
                .ifPresent(tw -> {
                    teamwerknemerRepository.findByTeamId(tw.getTeam().getId())
                            .forEach(teamlid -> {
                                if (!teamlid.getWerknemer().getId().equals(dto.werknemerId())) {
                                    notificatieService.maakNotificatie(
                                            teamlid.getWerknemer().getId(),
                                            "Teamlid afwezig",
                                            afwezigheidBericht);
                                    sseService.pushEvent(
                                            teamlid.getWerknemer().getId(),
                                            "afwezigheid_gemeld",
                                            Map.of("werknemerId", dto.werknemerId()));
                                }
                            });
                });

        teamRepository.findManagerByWerknemerId(dto.werknemerId())
                .forEach(manager -> {
                    notificatieService.maakNotificatie(
                            manager.getId(),
                            "Afwezigheid teamlid",
                            afwezigheidBericht);
                    sseService.pushEvent(
                            manager.getId(),
                            "afwezigheid_gemeld",
                            Map.of("werknemerId", dto.werknemerId()));
                });

        sseService.pushEvent(
                dto.werknemerId(),
                "afwezigheid_gemeld",
                Map.of("afwezigheidId", afwezigheid.getId()));

        return "Afwezigheid succesvol gemeld.";
    }

    public List<AfwezigheidsOverzichtDTO> getAlleAfwezigheden() {
        return afwezigheidRepository.findAllWithWerknemer().stream()
                .map(
                        a ->
                                new AfwezigheidsOverzichtDTO(
                                        a.getWerknemer().getId(),
                                        a.getWerknemer().getVoornaam(),
                                        a.getWerknemer().getNaam(),
                                        "Ziekte",
                                        a.getStartDatum(),
                                        a.getEindDatum(),
                                        null))
                .toList();
    }

    public List<AfwezigheidsOverzichtDTO> geefAfwezighedenVanWerknemer(Integer werknemerId) {
        return afwezigheidRepository.findByWerknemerId(werknemerId).stream()
                .map(
                        a ->
                                new AfwezigheidsOverzichtDTO(
                                        a.getWerknemer().getId(),
                                        a.getWerknemer().getVoornaam(),
                                        a.getWerknemer().getNaam(),
                                        "Ziekte",
                                        a.getStartDatum(),
                                        a.getEindDatum(),
                                        null))
                .toList();
    }

    public List<AfwezigheidsOverzichtDTO> geefAfwezighedenVanTeam() {
        Set<Integer> teamgenootIds = sessieService.getTeamgenootIds();
        return afwezigheidRepository.findAllWithWerknemer().stream()
                .filter(a -> teamgenootIds.contains(a.getWerknemer().getId()))
                .map(
                        a ->
                                new AfwezigheidsOverzichtDTO(
                                        a.getWerknemer().getId(),
                                        a.getWerknemer().getVoornaam(),
                                        a.getWerknemer().getNaam(),
                                        "Ziekte",
                                        a.getStartDatum(),
                                        a.getEindDatum(),
                                        null))
                .toList();
    }

    public long telHuidigeAfwezigen() {
        return afwezigheidRepository.telHuidigeAfwezigen();
    }
}

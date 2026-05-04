package hogent.sdp2.backend.REST.service.afwezigheid;

import hogent.sdp2.backend.domain.Afwezigheid;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.REST.dto.request.AfwezigheidAanmakenDTO;
import hogent.sdp2.backend.REST.dto.response.AfwezigheidsOverzichtDTO;
import hogent.sdp2.backend.REST.repository.AfwezigheidRepository;
import hogent.sdp2.backend.REST.repository.TeamwerknemerRepository;
import hogent.sdp2.backend.REST.repository.WerknemerRepository;
import hogent.sdp2.backend.REST.service.notificatie.NotificatieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AfwezigheidService {

    private final AfwezigheidRepository afwezigheidRepository;
    private final WerknemerRepository werknemerRepository;
    private final TeamwerknemerRepository teamwerknemerRepository;
    private final NotificatieService notificatieService;

    public String meldAfwezigheid(AfwezigheidAanmakenDTO dto) {
        Werknemer werknemer = werknemerRepository.findById(dto.werknemerId())
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

        // Notificatie naar alle teamleden
        teamwerknemerRepository.findByWerknemerId(dto.werknemerId())
                .stream()
                .findFirst()
                .ifPresent(tw -> {
                    teamwerknemerRepository.findByTeamId(tw.getTeam().getId())
                            .forEach(teamlid -> {
                                if (!teamlid.getWerknemer().getId().equals(dto.werknemerId())) {
                                    notificatieService.maakNotificatie(
                                            teamlid.getWerknemer().getId(),
                                            "Teamlid afwezig",
                                            werknemer.getVoornaam() + " " + werknemer.getNaam() +
                                                    " is ziek van " + dto.startDatum() + " tot " + dto.eindDatum() + "."
                                    );
                                }
                            });
                });

        return "Afwezigheid succesvol gemeld.";
    }

    public List<AfwezigheidsOverzichtDTO> getAlleAfwezigheden() {
        return afwezigheidRepository.findAllWithWerknemer().stream()
                .map(a -> new AfwezigheidsOverzichtDTO(
                        a.getWerknemer().getId(),
                        a.getWerknemer().getVoornaam(),
                        a.getWerknemer().getNaam(),
                        "Ziekte",
                        a.getStartDatum(),
                        a.getEindDatum(),
                        null
                ))
                .toList();
    }
}
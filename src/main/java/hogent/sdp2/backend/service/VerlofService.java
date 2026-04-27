package hogent.sdp2.backend.service;

import hogent.sdp2.backend.domain.Verlof;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.dto.request.VerlofAanvragenDTO;
import hogent.sdp2.backend.repository.TeamwerknemerRepository;
import hogent.sdp2.backend.repository.VerlofRepository;
import hogent.sdp2.backend.repository.WerknemerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerlofService {

    private final VerlofRepository verlofRepository;
    private final WerknemerRepository werknemerRepository;
    private final TeamwerknemerRepository teamwerknemerRepository;
    private final NotificatieService notificatieService;

    public String vraagVerlofAan(VerlofAanvragenDTO dto) {
        Werknemer werknemer = werknemerRepository.findById(dto.werknemerId())
                .orElseThrow(() -> new RuntimeException("Werknemer niet gevonden"));

        if (dto.eindDatum().isBefore(dto.startDatum())) {
            throw new RuntimeException("Einddatum mag niet voor startdatum liggen.");
        }

        Integer goedkeurderId = teamwerknemerRepository
                .findByWerknemerId(dto.werknemerId())
                .stream()
                .findFirst()
                .flatMap(tw -> teamwerknemerRepository.findGoedkeurderVanTeam(tw.getTeam().getId()))
                .map(tw -> tw.getWerknemer().getId())
                .orElse(null);

        if (goedkeurderId == null) {
            throw new RuntimeException("Geen manager gevonden voor deze werknemer.");
        }

        Verlof verlof = new Verlof();
        verlof.setWerknemer(werknemer);
        verlof.setStartDatum(dto.startDatum());
        verlof.setEindDatum(dto.eindDatum());
        verlof.setType(dto.type());
        verlof.setStatus("In afwachting");
        verlof.setGoedkeurderId(goedkeurderId);

        verlofRepository.save(verlof);

        notificatieService.maakNotificatie(
                goedkeurderId,
                "Nieuwe verlofaanvraag",
                werknemer.getVoornaam() + " " + werknemer.getNaam() + " heeft verlof aangevraagd van " +
                        dto.startDatum() + " tot " + dto.eindDatum() + ".",
                verlof.getId()
        );

        return "Verlofaanvraag succesvol ingediend.";
    }

    public String geefVerlofStatus(Integer verlofId) {
        return verlofRepository.findById(verlofId)
                .map(Verlof::getStatus)
                .orElseThrow(() -> new RuntimeException("Verlof niet gevonden"));
    }

    public String keurVerlofGoed(Integer verlofId) {
        Verlof verlof = verlofRepository.findById(verlofId)
                .orElseThrow(() -> new RuntimeException("Verlof niet gevonden"));

        verlof.setStatus("Goedgekeurd");
        verlofRepository.save(verlof);

        notificatieService.maakNotificatie(
                verlof.getWerknemer().getId(),
                "Verlof goedgekeurd",
                "Jouw verlofaanvraag van " + verlof.getStartDatum() + " tot " + verlof.getEindDatum() + " is goedgekeurd.",
                verlof.getId()
        );

        return "Verlof goedgekeurd.";
    }

    public String wijsVerlofAf(Integer verlofId) {
        Verlof verlof = verlofRepository.findById(verlofId)
                .orElseThrow(() -> new RuntimeException("Verlof niet gevonden"));

        verlof.setStatus("Afgewezen");
        verlofRepository.save(verlof);

        notificatieService.maakNotificatie(
                verlof.getWerknemer().getId(),
                "Verlof afgewezen",
                "Jouw verlofaanvraag van " + verlof.getStartDatum() + " tot " + verlof.getEindDatum() + " is afgewezen."
        );

        return "Verlof afgewezen.";
    }

    public String annuleerVerlof(Integer verlofId) {
        Verlof verlof = verlofRepository.findById(verlofId)
                .orElseThrow(() -> new RuntimeException("Verlof niet gevonden"));

        verlof.setStatus("Geannuleerd");
        verlofRepository.save(verlof);

        Werknemer werknemer = verlof.getWerknemer();

        teamwerknemerRepository.findByWerknemerId(werknemer.getId())
                .stream()
                .findFirst()
                .ifPresent(tw -> {
                    teamwerknemerRepository.findByTeamId(tw.getTeam().getId())
                            .forEach(teamlid -> {
                                if (!teamlid.getWerknemer().getId().equals(werknemer.getId())) {
                                    notificatieService.maakNotificatie(
                                            teamlid.getWerknemer().getId(),
                                            "Verlof geannuleerd",
                                            werknemer.getVoornaam() + " " + werknemer.getNaam() +
                                                    " heeft zijn verlof van " + verlof.getStartDatum() +
                                                    " tot " + verlof.getEindDatum() + " geannuleerd."
                                    );
                                }
                            });
                });

        return "Verlof geannuleerd.";
    }
}
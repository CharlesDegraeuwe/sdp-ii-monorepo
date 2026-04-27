package hogent.sdp2.backend.service;

import hogent.sdp2.backend.dto.response.AfwezigheidsOverzichtDTO;
import hogent.sdp2.backend.repository.AfwezigheidRepository;
import hogent.sdp2.backend.repository.TeamwerknemerRepository;
import hogent.sdp2.backend.repository.VerlofRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanningService {

    private final TeamwerknemerRepository teamwerknemerRepository;
    private final AfwezigheidRepository afwezigheidRepository;
    private final VerlofRepository verlofRepository;

    public List<AfwezigheidsOverzichtDTO> geefAfwezighedenVanTeam(Integer werknemerId, LocalDate van, LocalDate tot) {
        List<AfwezigheidsOverzichtDTO> resultaat = new ArrayList<>();

        // Haal alle teamleden op via het team van de ingelogde werknemer
        teamwerknemerRepository.findByWerknemerId(werknemerId)
                .stream()
                .findFirst()
                .ifPresent(tw -> {
                    teamwerknemerRepository.findByTeamId(tw.getTeam().getId())
                            .forEach(teamlid -> {
                                Integer teamlidId = teamlid.getWerknemer().getId();
                                String voornaam = teamlid.getWerknemer().getVoornaam();
                                String naam = teamlid.getWerknemer().getNaam();

                                // Afwezigheden (ziekte)
                                afwezigheidRepository.findByWerknemerId(teamlidId)
                                        .stream()
                                        .filter(a -> !a.getEindDatum().isBefore(van) && !a.getStartDatum().isAfter(tot))
                                        .forEach(a -> resultaat.add(new AfwezigheidsOverzichtDTO(
                                                teamlidId, voornaam, naam,
                                                "Ziekte",
                                                a.getStartDatum(), a.getEindDatum(),
                                                null
                                        )));

                                // Verloven (enkel goedgekeurde)
                                verlofRepository.findByWerknemerId(teamlidId)
                                        .stream()
                                        .filter(v -> !v.getEindDatum().isBefore(van) && !v.getStartDatum().isAfter(tot))
                                        .filter(v -> v.getStatus().equals("Goedgekeurd") || v.getStatus().equals("In afwachting"))
                                        .forEach(v -> resultaat.add(new AfwezigheidsOverzichtDTO(
                                                teamlidId, voornaam, naam,
                                                "Verlof",
                                                v.getStartDatum(), v.getEindDatum(),
                                                v.getStatus()
                                        )));
                            });
                });

        return resultaat;
    }
}
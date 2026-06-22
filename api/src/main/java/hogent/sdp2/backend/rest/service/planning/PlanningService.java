package hogent.sdp2.backend.rest.service.planning;

import hogent.sdp2.backend.rest.dto.response.AfwezigheidsOverzichtDTO;
import hogent.sdp2.backend.rest.repository.AfwezigheidRepository;
import hogent.sdp2.backend.rest.repository.TeamwerknemerRepository;
import hogent.sdp2.backend.rest.repository.VerlofRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanningService {

    private final TeamwerknemerRepository teamwerknemerRepository;
    private final AfwezigheidRepository afwezigheidRepository;
    private final VerlofRepository verlofRepository;

    public List<AfwezigheidsOverzichtDTO> geefAfwezighedenVanTeam(
            Integer werknemerId, LocalDate van, LocalDate tot) {
        List<AfwezigheidsOverzichtDTO> resultaat = new ArrayList<>();

        // Haal alle teamleden op via het team van de ingelogde werknemer
        teamwerknemerRepository.findByWerknemerId(werknemerId).stream()
                .findFirst()
                .ifPresent(
                        tw -> {
                            teamwerknemerRepository
                                    .findByTeamId(tw.getTeam().getId())
                                    .forEach(
                                            teamlid -> {
                                                Integer teamlidId = teamlid.getWerknemer().getId();
                                                String voornaam =
                                                        teamlid.getWerknemer().getVoornaam();
                                                String naam = teamlid.getWerknemer().getNaam();

                                                // Afwezigheden (ziekte)
                                                afwezigheidRepository
                                                        .findByWerknemerId(teamlidId)
                                                        .stream()
                                                        .filter(
                                                                a ->
                                                                        !a.getEindDatum()
                                                                                        .isBefore(
                                                                                                van)
                                                                                && !a.getStartDatum()
                                                                                        .isAfter(
                                                                                                tot))
                                                        .forEach(
                                                                a ->
                                                                        resultaat.add(
                                                                                new AfwezigheidsOverzichtDTO(
                                                                                        teamlidId,
                                                                                        voornaam,
                                                                                        naam,
                                                                                        "Ziekte",
                                                                                        a
                                                                                                .getStartDatum(),
                                                                                        a
                                                                                                .getEindDatum(),
                                                                                        null)));

                                                // Verloven (enkel goedgekeurde)
                                                verlofRepository
                                                        .findByWerknemerId(teamlidId)
                                                        .stream()
                                                        .filter(
                                                                v ->
                                                                        !v.getEindDatum()
                                                                                        .isBefore(
                                                                                                van)
                                                                                && !v.getStartDatum()
                                                                                        .isAfter(
                                                                                                tot))
                                                        .filter(
                                                                v ->
                                                                        v.getStatus()
                                                                                        .equals(
                                                                                                "Goedgekeurd")
                                                                                || v.getStatus()
                                                                                        .equals(
                                                                                                "In afwachting"))
                                                        .forEach(
                                                                v ->
                                                                        resultaat.add(
                                                                                new AfwezigheidsOverzichtDTO(
                                                                                        teamlidId,
                                                                                        voornaam,
                                                                                        naam,
                                                                                        "Verlof",
                                                                                        v
                                                                                                .getStartDatum(),
                                                                                        v
                                                                                                .getEindDatum(),
                                                                                        v
                                                                                                .getStatus())));
                                            });
                        });

        return resultaat;
    }

    public List<AfwezigheidsOverzichtDTO> geefPlanningVanWerknemer(
            Integer werknemerId, LocalDate van, LocalDate tot) {
        List<AfwezigheidsOverzichtDTO> resultaat = new ArrayList<>();

        var werknemer =
                afwezigheidRepository.findByWerknemerId(werknemerId).stream()
                        .findFirst()
                        .map(a -> a.getWerknemer())
                        .orElse(null);

        String voornaam = "";
        String naam = "";

        if (werknemer != null) {
            voornaam = werknemer.getVoornaam();
            naam = werknemer.getNaam();
        } else {
            var verloven = verlofRepository.findByWerknemerId(werknemerId);
            if (!verloven.isEmpty()) {
                voornaam = verloven.get(0).getWerknemer().getVoornaam();
                naam = verloven.get(0).getWerknemer().getNaam();
            }
        }

        String finalVoornaam = voornaam;
        String finalNaam = naam;

        afwezigheidRepository.findByWerknemerId(werknemerId).stream()
                .filter(a -> !a.getEindDatum().isBefore(van) && !a.getStartDatum().isAfter(tot))
                .forEach(
                        a ->
                                resultaat.add(
                                        new AfwezigheidsOverzichtDTO(
                                                werknemerId,
                                                finalVoornaam,
                                                finalNaam,
                                                "Ziekte",
                                                a.getStartDatum(),
                                                a.getEindDatum(),
                                                null)));

        verlofRepository.findByWerknemerId(werknemerId).stream()
                .filter(v -> !v.getEindDatum().isBefore(van) && !v.getStartDatum().isAfter(tot))
                .filter(
                        v ->
                                v.getStatus().equals("Goedgekeurd")
                                        || v.getStatus().equals("In afwachting"))
                .forEach(
                        v ->
                                resultaat.add(
                                        new AfwezigheidsOverzichtDTO(
                                                werknemerId,
                                                finalVoornaam,
                                                finalNaam,
                                                "Verlof",
                                                v.getStartDatum(),
                                                v.getEindDatum(),
                                                v.getStatus())));

        return resultaat;
    }
}

package hogent.sdp2.backend.rest.service.afwezigheid;

import hogent.sdp2.backend.rest.dto.response.GeschiedenisItemDTO;
import hogent.sdp2.backend.rest.dto.response.GeschiedenisItemMetWerknemerDTO;
import hogent.sdp2.backend.rest.dto.response.TeamMetLedenDTO;
import hogent.sdp2.backend.rest.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.rest.repository.AfwezigheidRepository;
import hogent.sdp2.backend.rest.repository.TeamRepository;
import hogent.sdp2.backend.rest.repository.TeamwerknemerRepository;
import hogent.sdp2.backend.rest.repository.VerlofRepository;
import hogent.sdp2.backend.rest.repository.WerknemerRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeschiedenisService {

    private final VerlofRepository verlofRepository;
    private final AfwezigheidRepository afwezigheidRepository;
    private final TeamwerknemerRepository teamwerknemerRepository;
    private final TeamRepository teamRepository;
    private final WerknemerRepository werknemerRepository;

    public List<GeschiedenisItemDTO> geefGeschiedenisVanWerknemer(Integer werknemerId) {
        List<GeschiedenisItemDTO> resultaat = new ArrayList<>();

        verlofRepository
                .findByWerknemerId(werknemerId)
                .forEach(
                        v ->
                                resultaat.add(
                                        new GeschiedenisItemDTO(
                                                v.getId(),
                                                "Verlof",
                                                v.getStartDatum(),
                                                v.getEindDatum(),
                                                v.getStatus(),
                                                v.getType())));

        afwezigheidRepository
                .findByWerknemerId(werknemerId)
                .forEach(
                        a ->
                                resultaat.add(
                                        new GeschiedenisItemDTO(
                                                a.getId(),
                                                "Ziekte",
                                                a.getStartDatum(),
                                                a.getEindDatum(),
                                                null,
                                                a.getReden())));

        resultaat.sort(Comparator.comparing(GeschiedenisItemDTO::startDatum).reversed());
        return resultaat;
    }

    public List<WerknemerResponseDTO> geefTeamledenVanManager(Integer managerId) {
        return teamRepository.findByManagerId(managerId).stream()
                .flatMap(team -> teamwerknemerRepository.findByTeamId(team.getId()).stream())
                .map(
                        tl -> {
                            var w = tl.getWerknemer();
                            return new WerknemerResponseDTO(
                                    w.getId(),
                                    w.getNaam(),
                                    w.getVoornaam(),
                                    w.getEmail(),
                                    w.getTelefoonnummer(),
                                    w.getGeboortedatum(),
                                    w.getRol(),
                                    w.getStatus());
                        })
                .collect(
                        Collectors.collectingAndThen(
                                Collectors.toMap(WerknemerResponseDTO::id, w -> w, (a, b) -> a),
                                m -> new ArrayList<>(m.values())));
    }

    public List<TeamMetLedenDTO> geefTeamsVanManager(Integer managerId) {
        return teamRepository.findByManagerId(managerId).stream()
                .map(
                        team -> {
                            List<WerknemerResponseDTO> leden =
                                    teamwerknemerRepository.findByTeamId(team.getId()).stream()
                                            .map(
                                                    tw -> {
                                                        var w = tw.getWerknemer();
                                                        return new WerknemerResponseDTO(
                                                                w.getId(),
                                                                w.getNaam(),
                                                                w.getVoornaam(),
                                                                w.getEmail(),
                                                                w.getTelefoonnummer(),
                                                                w.getGeboortedatum(),
                                                                w.getRol(),
                                                                w.getStatus());
                                                    })
                                            .toList();
                            return new TeamMetLedenDTO(team.getId(), team.getNaam(), leden);
                        })
                .toList();
    }

    public List<GeschiedenisItemMetWerknemerDTO> geefTeamOverzicht(Integer teamId) {
        return teamwerknemerRepository.findByTeamId(teamId).stream()
                .flatMap(
                        tw -> {
                            var w = tw.getWerknemer();
                            List<GeschiedenisItemMetWerknemerDTO> items = new ArrayList<>();

                            verlofRepository
                                    .findByWerknemerId(w.getId())
                                    .forEach(
                                            v ->
                                                    items.add(
                                                            new GeschiedenisItemMetWerknemerDTO(
                                                                    v.getId(),
                                                                    "Verlof",
                                                                    v.getStartDatum(),
                                                                    v.getEindDatum(),
                                                                    v.getStatus(),
                                                                    v.getType(),
                                                                    w.getId(),
                                                                    w.getVoornaam(),
                                                                    w.getNaam())));

                            afwezigheidRepository
                                    .findByWerknemerId(w.getId())
                                    .forEach(
                                            a ->
                                                    items.add(
                                                            new GeschiedenisItemMetWerknemerDTO(
                                                                    a.getId(),
                                                                    "Ziekte",
                                                                    a.getStartDatum(),
                                                                    a.getEindDatum(),
                                                                    null,
                                                                    a.getReden(),
                                                                    w.getId(),
                                                                    w.getVoornaam(),
                                                                    w.getNaam())));

                            return items.stream();
                        })
                .sorted(
                        Comparator.comparing(GeschiedenisItemMetWerknemerDTO::werknemerNaam)
                                .thenComparing(GeschiedenisItemMetWerknemerDTO::werknemerVoornaam)
                                .thenComparing(
                                        Comparator.comparing(
                                                        GeschiedenisItemMetWerknemerDTO::startDatum)
                                                .reversed()))
                .toList();
    }
}

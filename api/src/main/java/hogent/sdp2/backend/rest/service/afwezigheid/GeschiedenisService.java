package hogent.sdp2.backend.rest.service.afwezigheid;

import hogent.sdp2.backend.rest.dto.response.GeschiedenisItemDTO;
import hogent.sdp2.backend.rest.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.rest.repository.AfwezigheidRepository;
import hogent.sdp2.backend.rest.repository.TeamwerknemerRepository;
import hogent.sdp2.backend.rest.repository.VerlofRepository;
import hogent.sdp2.backend.rest.repository.WerknemerRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeschiedenisService {

    private final VerlofRepository verlofRepository;
    private final AfwezigheidRepository afwezigheidRepository;
    private final TeamwerknemerRepository teamwerknemerRepository;
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
        return teamwerknemerRepository.findByWerknemerId(managerId).stream()
                .findFirst()
                .map(
                        tw ->
                                teamwerknemerRepository.findByTeamId(tw.getTeam().getId()).stream()
                                        .filter(tl -> !tl.getWerknemer().getId().equals(managerId))
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
                                        .toList())
                .orElse(List.of());
    }
}

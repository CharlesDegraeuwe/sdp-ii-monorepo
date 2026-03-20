package hogent.sdp2.backend.service;

import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.dto.request.TeamResponseDTO;
import hogent.sdp2.backend.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.repository.TeamRepository;
import hogent.sdp2.backend.repository.TeamwerknemerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamwerknemerRepository teamwerknemerRepository;

    public List<TeamResponseDTO> geefTeams() {
        return teamRepository.findAll().stream().map(
                t -> new TeamResponseDTO(t.getId(), t.getNaam())).toList();

    }
    public List<TeamResponseDTO> geefTeamsVanSite(Integer siteId) {
        return teamRepository.findBySiteId(siteId).stream()
                .map(t -> new TeamResponseDTO(t.getId(), t.getNaam()))
                .toList();
    }

    public List<WerknemerResponseDTO> geefWerknemersVanTeam(Integer teamId) {
        return teamwerknemerRepository.findByTeamId(teamId).stream()
                .map(tw -> {
                    Werknemer w = tw.getWerknemer();
                    return new WerknemerResponseDTO(w.getId(), w.getNaam(), w.getVoornaam(),
                            w.getEmail(), w.getTelefoonnummer(), w.getGeboortedatum(),
                            w.getRol(), w.getStatus());
                })
                .toList();
    }

    public List<WerknemerResponseDTO> voegToeAanTeam(int teamId, int werknemerId) {
        return null;
    }
}
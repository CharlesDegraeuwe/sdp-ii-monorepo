package hogent.sdp2.backend.service;

import hogent.sdp2.backend.domain.Teamwerknemer;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.dto.request.TeamResponseDTO;
import hogent.sdp2.backend.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.repository.TeamRepository;
import hogent.sdp2.backend.repository.TeamwerknemerRepository;
import hogent.sdp2.backend.repository.WerknemerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamwerknemerRepository teamwerknemerRepository;
    private final WerknemerRepository werknemerRepository;

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
        var team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team niet gevonden"));

        long aantalLeden = teamwerknemerRepository.countByTeamId(teamId);
        if (aantalLeden >= 4) {
            throw new RuntimeException("Team zit vol (max 4 leden)");
        }

        var werknemer = werknemerRepository.findById(werknemerId)
                .orElseThrow(() -> new RuntimeException("Werknemer niet gevonden"));

        boolean bestaatAl = teamwerknemerRepository.existsByTeamIdAndWerknemerId(teamId, werknemerId);
        if (bestaatAl) {
            throw new RuntimeException("Werknemer zit al in dit team");
        }

        var teamwerknemer = new Teamwerknemer();
        teamwerknemer.setTeam(team);
        teamwerknemer.setWerknemer(werknemer);
        teamwerknemerRepository.save(teamwerknemer);

        return geefWerknemersVanTeam(teamId);
    }

    public List<WerknemerResponseDTO> geefBeschikbareWerknemers(int teamId) {
        List<Integer> huidigeIds = teamwerknemerRepository.findByTeamId(teamId).stream()
                .map(tw -> tw.getWerknemer().getId())
                .toList();

        return werknemerRepository.findAll().stream()
                .filter(w -> !huidigeIds.contains(w.getId()))
                .map(w -> new WerknemerResponseDTO(w.getId(), w.getNaam(), w.getVoornaam(),
                        w.getEmail(), w.getTelefoonnummer(), w.getGeboortedatum(),
                        w.getRol(), w.getStatus()))
                .toList();
    }
}
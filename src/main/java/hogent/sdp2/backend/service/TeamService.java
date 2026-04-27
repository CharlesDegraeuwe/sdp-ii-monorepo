package hogent.sdp2.backend.service;

import hogent.sdp2.backend.domain.*;
import hogent.sdp2.backend.dto.request.CreateTeamRequestDTO;
import hogent.sdp2.backend.dto.request.TeamResponseDTO;
import hogent.sdp2.backend.dto.response.SiteResponseDTO;
import hogent.sdp2.backend.dto.response.TeamLidResponseDTO;
import hogent.sdp2.backend.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamwerknemerRepository teamwerknemerRepository;
    private final WerknemerRepository werknemerRepository;
    private final SiteRepository siteRepository;
    private final SiteteamRepository siteteamRepository;

    public List<TeamResponseDTO> geefTeams() {
        return teamRepository.findAll().stream()
                .map(this::toTeamResponse)
                .toList();
    }

    public List<TeamResponseDTO> geefTeamsVanSite(Integer siteId) {
        return teamRepository.findBySiteId(siteId).stream()
                .map(this::toTeamResponse)
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

    public List<TeamLidResponseDTO> geefTeamLedenMetSupervisor(Integer teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team niet gevonden"));

        String managerNaam = null;
        Integer managerId = null;
        if (team.getManager() != null) {
            managerId = team.getManager().getId();
            managerNaam = team.getManager().getVoornaam() + " " + team.getManager().getNaam();
        }

        Integer siteId = null;
        String siteNaam = null;
        List<Siteteam> sitelinks = siteteamRepository.findByTeamId(teamId);
        if (!sitelinks.isEmpty()) {
            Site site = sitelinks.get(0).getSite();
            siteId = site.getId();
            siteNaam = site.getNaam();
        }

        final String mNaam = managerNaam;
        final Integer mId = managerId;
        final Integer sId = siteId;
        final String sNaam = siteNaam;

        return teamwerknemerRepository.findByTeamId(teamId).stream()
                .map(tw -> {
                    Werknemer w = tw.getWerknemer();
                    return new TeamLidResponseDTO(
                            w.getId(), w.getNaam(), w.getVoornaam(),
                            w.getEmail(), w.getTelefoonnummer(), w.getRol(),
                            tw.getIsSupervisor(),
                            team.getId(), team.getNaam(), team.getBeschrijving(),
                            mId, mNaam, sId, sNaam
                    );
                })
                .toList();
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

    public List<WerknemerResponseDTO> geefAlleWerknemers() {
        return werknemerRepository.findAll().stream()
                .map(w -> new WerknemerResponseDTO(w.getId(), w.getNaam(), w.getVoornaam(),
                        w.getEmail(), w.getTelefoonnummer(), w.getGeboortedatum(),
                        w.getRol(), w.getStatus()))
                .toList();
    }

    public List<SiteResponseDTO> geefAlleSites() {
        return siteRepository.findAll().stream()
                .map(s -> new SiteResponseDTO(s.getId(), s.getNaam(), s.getLocatie()))
                .toList();
    }

    //deze was ysu fktp
    @Transactional
    public TeamResponseDTO maakTeam(CreateTeamRequestDTO dto) {
        Team team = new Team();
        team.setNaam(dto.naam());
        team.setBeschrijving(dto.beschrijving());

        if (dto.managerId() != null) {
            Werknemer manager = werknemerRepository.findById(dto.managerId())
                    .orElseThrow(() -> new RuntimeException("Manager niet gevonden"));
            team.setManager(manager);
        }

        team = teamRepository.save(team);

        if (dto.siteId() != null) {
            Site site = siteRepository.findById(dto.siteId())
                    .orElseThrow(() -> new RuntimeException("Site niet gevonden"));
            Siteteam siteteam = new Siteteam();
            SiteteamId stId = new SiteteamId();
            stId.setSiteId(site.getId());
            stId.setTeamId(team.getId());
            siteteam.setId(stId);
            siteteam.setSite(site);
            siteteam.setTeam(team);
            siteteamRepository.save(siteteam);
        }

        //aantal check
        if (dto.leden() != null) {
            int count = 0;
            for (var lid : dto.leden()) {
                if (count >= 4) break;
                Werknemer w = werknemerRepository.findById(lid.werknemerId())
                        .orElseThrow(() -> new RuntimeException("Werknemer niet gevonden"));
                Teamwerknemer tw = new Teamwerknemer();
                TeamwerknemerId twId = new TeamwerknemerId();
                twId.setTeamId(team.getId());
                twId.setWerknemerId(w.getId());
                tw.setId(twId);
                tw.setTeam(team);
                tw.setWerknemer(w);
                tw.setIsSupervisor(lid.isSupervisor());
                teamwerknemerRepository.save(tw);

                if (lid.isSupervisor() && !"Supervisor".equals(w.getRol())) {
                    w.setRol("Supervisor");
                    werknemerRepository.save(w);
                }

                count++;
            }
        }

        return toTeamResponse(team);
    }

    @Transactional
    public List<WerknemerResponseDTO> voegToeAanTeam(int teamId, int werknemerId) {
        long aantalLeden = teamwerknemerRepository.countByTeamId(teamId);
        if (aantalLeden >= 4) {
            throw new RuntimeException("Team zit vol (max 4 leden)");
        }
        if (teamwerknemerRepository.existsByTeamIdAndWerknemerId(teamId, werknemerId)) {
            throw new RuntimeException("Werknemer zit al in dit team");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team niet gevonden"));
        Werknemer werknemer = werknemerRepository.findById(werknemerId)
                .orElseThrow(() -> new RuntimeException("Werknemer niet gevonden"));

        Teamwerknemer tw = new Teamwerknemer();
        TeamwerknemerId twId = new TeamwerknemerId();
        twId.setTeamId(teamId);
        twId.setWerknemerId(werknemerId);
        tw.setId(twId);
        tw.setTeam(team);
        tw.setWerknemer(werknemer);
        tw.setIsSupervisor(false);
        teamwerknemerRepository.save(tw);

        return geefWerknemersVanTeam(teamId);
    }

    private TeamResponseDTO toTeamResponse(Team t) {
        String managerNaam = null;
        Integer managerId = null;
        if (t.getManager() != null) {
            managerId = t.getManager().getId();
            managerNaam = t.getManager().getVoornaam() + " " + t.getManager().getNaam();
        }

        Integer siteId = null;
        String siteNaam = null;
        List<Siteteam> sitelinks = siteteamRepository.findByTeamId(t.getId());
        if (!sitelinks.isEmpty()) {
            Site site = sitelinks.get(0).getSite();
            siteId = site.getId();
            siteNaam = site.getNaam();
        }

        return new TeamResponseDTO(t.getId(), t.getNaam(), t.getBeschrijving(),
                managerId, managerNaam, siteId, siteNaam);
    }

    public List<TeamResponseDTO> geefTeamsVanWerknemer(int werknemerId) {
        return teamwerknemerRepository.findByWerknemerId(werknemerId).stream()
                .map(tw -> toTeamResponse(tw.getTeam()))
                .toList();
    }

    @Transactional
    public List<WerknemerResponseDTO> verwijderUitTeam(int teamId, int werknemerId) {
        TeamwerknemerId twId = new TeamwerknemerId();
        twId.setTeamId(teamId);
        twId.setWerknemerId(werknemerId);

        boolean wasSupervisor = false;

        var optTw = teamwerknemerRepository.findById(twId);
        if (optTw.isPresent() && optTw.get().getIsSupervisor()) {
            wasSupervisor = true;
            Werknemer w = optTw.get().getWerknemer();
            w.setRol("Werknemer");
            werknemerRepository.save(w);
        }

        teamwerknemerRepository.deleteById(twId);

        if (wasSupervisor) {
            List<Teamwerknemer> overigeLeden = teamwerknemerRepository.findByTeamId(teamId);
            if (!overigeLeden.isEmpty()) {
                Teamwerknemer nieuweSupervisor = overigeLeden.get(0);
                nieuweSupervisor.setIsSupervisor(true);
                teamwerknemerRepository.save(nieuweSupervisor);

                Werknemer w = nieuweSupervisor.getWerknemer();
                w.setRol("Supervisor");
                werknemerRepository.save(w);
            }
        }

        return geefWerknemersVanTeam(teamId);
    }

    @Transactional
    public List<TeamResponseDTO> verwijderTeam(int teamId) {
        teamRepository.deleteById(teamId);
        return geefTeams();
    }

    @Transactional
    public void maakSupervisor(int teamId, int werknemerId) {
        teamwerknemerRepository.findByTeamId(teamId).forEach(tw -> {
            if (tw.getIsSupervisor()) {
                tw.setIsSupervisor(false);
                teamwerknemerRepository.save(tw);
                tw.getWerknemer().setRol("Werknemer");
                werknemerRepository.save(tw.getWerknemer());
            }
        });

        TeamwerknemerId twId = new TeamwerknemerId();
        twId.setTeamId(teamId);
        twId.setWerknemerId(werknemerId);
        teamwerknemerRepository.findById(twId).ifPresent(tw -> {
            tw.setIsSupervisor(true);
            teamwerknemerRepository.save(tw);
            tw.getWerknemer().setRol("Supervisor");
            werknemerRepository.save(tw.getWerknemer());
        });
    }


}
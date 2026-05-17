package hogent.sdp2.backend.service;

import hogent.sdp2.backend.domain.*;
import hogent.sdp2.backend.rest.dto.request.CreateTeamRequestDTO;
import hogent.sdp2.backend.rest.dto.request.TeamLidRequestDTO;
import hogent.sdp2.backend.rest.dto.request.TeamResponseDTO;
import hogent.sdp2.backend.rest.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.rest.repository.*;
import hogent.sdp2.backend.rest.service.teams.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock private TeamRepository teamRepository;
    @Mock private TeamwerknemerRepository teamwerknemerRepository;
    @Mock private WerknemerRepository werknemerRepository;
    @Mock private SiteRepository siteRepository;
    @Mock private SiteteamRepository siteteamRepository;

    @InjectMocks private TeamService teamService;

    private Werknemer werknemer;
    private Werknemer manager;
    private Team team;

    @BeforeEach
    void setUp() {
        werknemer = new Werknemer();
        werknemer.setId(1);
        werknemer.setNaam("Janssen");
        werknemer.setVoornaam("Jan");
        werknemer.setEmail("jan@test.be");
        werknemer.setTelefoonnummer("0499000000");
        werknemer.setGeboortedatum(LocalDate.of(1990, 1, 1));
        werknemer.setRol("Werknemer");
        werknemer.setStatus("Actief");
        werknemer.setWachtwoord("hash");

        manager = new Werknemer();
        manager.setId(10);
        manager.setNaam("Manager");
        manager.setVoornaam("Marc");
        manager.setEmail("marc@test.be");
        manager.setTelefoonnummer("0499111111");
        manager.setGeboortedatum(LocalDate.of(1985, 5, 15));
        manager.setRol("Manager");
        manager.setStatus("Actief");
        manager.setWachtwoord("hash");

        team = new Team();
        team.setId(1);
        team.setNaam("Team Alpha");
        team.setBeschrijving("Beschrijving");
        team.setManager(manager);
    }

    @Test
    void geefTeams_geeftAllTeamsTerug() {
        when(teamRepository.findAll()).thenReturn(List.of(team));
        when(siteteamRepository.findByTeamId(1)).thenReturn(List.of());

        List<TeamResponseDTO> result = teamService.geefTeams();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).naam()).isEqualTo("Team Alpha");
    }

    @Test
    void geefTeams_geeftLegeListBijGeenTeams() {
        when(teamRepository.findAll()).thenReturn(List.of());

        List<TeamResponseDTO> result = teamService.geefTeams();

        assertThat(result).isEmpty();
    }

    @Test
    void geefAlleWerknemers_geeftAlleWerknemersTerug() {
        when(werknemerRepository.findAll()).thenReturn(List.of(werknemer, manager));

        List<WerknemerResponseDTO> result = teamService.geefAlleWerknemers();

        assertThat(result).hasSize(2);
    }

    @Test
    void geefBeschikbareWerknemers_sluitTeamledenUit() {
        TeamwerknemerId twId = new TeamwerknemerId();
        twId.setTeamId(1);
        twId.setWerknemerId(1);
        Teamwerknemer tw = new Teamwerknemer();
        tw.setId(twId);
        tw.setWerknemer(werknemer);
        tw.setTeam(team);
        tw.setIsSupervisor(false);

        when(teamwerknemerRepository.findByTeamId(1)).thenReturn(List.of(tw));
        when(werknemerRepository.findAll()).thenReturn(List.of(werknemer, manager));

        List<WerknemerResponseDTO> result = teamService.geefBeschikbareWerknemers(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).voornaam()).isEqualTo("Marc");
    }

    @Test
    void maakTeam_maaktTeamAan() {
        CreateTeamRequestDTO dto = new CreateTeamRequestDTO("Nieuw Team", "Omschrijving", 10, null, List.of());

        when(werknemerRepository.findById(10)).thenReturn(Optional.of(manager));
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> {
            Team t = inv.getArgument(0);
            t.setId(99);
            return t;
        });
        when(siteteamRepository.findByTeamId(99)).thenReturn(List.of());

        TeamResponseDTO result = teamService.maakTeam(dto);

        assertThat(result.naam()).isEqualTo("Nieuw Team");
        assertThat(result.managerId()).isEqualTo(10);
        verify(teamRepository, times(1)).save(any(Team.class));
    }

    @Test
    void maakTeam_gooidExceptionBijOnbekendeManager() {
        CreateTeamRequestDTO dto = new CreateTeamRequestDTO("Team", "Omschrijving", 999, null, List.of());
        when(werknemerRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teamService.maakTeam(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Manager niet gevonden");
    }

    @Test
    void maakTeam_voegMaxVierLedenToe() {
        List<TeamLidRequestDTO> leden = List.of(
                new TeamLidRequestDTO(1, false),
                new TeamLidRequestDTO(2, false),
                new TeamLidRequestDTO(3, false),
                new TeamLidRequestDTO(4, false),
                new TeamLidRequestDTO(5, false)
        );
        CreateTeamRequestDTO dto = new CreateTeamRequestDTO("Team", "Omschrijving", null, null, leden);

        Werknemer w1 = maakWerknemer(1); Werknemer w2 = maakWerknemer(2);
        Werknemer w3 = maakWerknemer(3); Werknemer w4 = maakWerknemer(4);
        Werknemer w5 = maakWerknemer(5);

        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> {
            Team t = inv.getArgument(0); t.setId(1); return t;
        });
        when(siteteamRepository.findByTeamId(1)).thenReturn(List.of());
        when(werknemerRepository.findById(1)).thenReturn(Optional.of(w1));
        when(werknemerRepository.findById(2)).thenReturn(Optional.of(w2));
        when(werknemerRepository.findById(3)).thenReturn(Optional.of(w3));
        when(werknemerRepository.findById(4)).thenReturn(Optional.of(w4));

        teamService.maakTeam(dto);

        // max 4 leden, w5 wordt nooit opgezocht
        verify(werknemerRepository, never()).findById(5);
        verify(teamwerknemerRepository, times(4)).save(any(Teamwerknemer.class));
    }

    @Test
    void voegToeAanTeam_voegdWerknemerToe() {
        when(teamwerknemerRepository.countByTeamId(1)).thenReturn(2L);
        when(teamwerknemerRepository.existsByTeamIdAndWerknemerId(1, 1)).thenReturn(false);
        when(teamRepository.findById(1)).thenReturn(Optional.of(team));
        when(werknemerRepository.findById(1)).thenReturn(Optional.of(werknemer));
        when(teamwerknemerRepository.save(any())).thenReturn(null);
        when(teamwerknemerRepository.findByTeamId(1)).thenReturn(List.of());

        assertThatNoException().isThrownBy(() -> teamService.voegToeAanTeam(1, 1));
        verify(teamwerknemerRepository, times(1)).save(any(Teamwerknemer.class));
    }

    @Test
    void voegToeAanTeam_gooidExceptionAlsTeamVol() {
        when(teamwerknemerRepository.countByTeamId(1)).thenReturn(4L);

        assertThatThrownBy(() -> teamService.voegToeAanTeam(1, 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Team zit vol");
    }

    @Test
    void voegToeAanTeam_gooidExceptionAlsWerknemerAlInTeam() {
        when(teamwerknemerRepository.countByTeamId(1)).thenReturn(2L);
        when(teamwerknemerRepository.existsByTeamIdAndWerknemerId(1, 1)).thenReturn(true);

        assertThatThrownBy(() -> teamService.voegToeAanTeam(1, 1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("zit al in dit team");
    }

    @Test
    void verwijderTeam_verwijdertTeam() {
        when(teamRepository.findAll()).thenReturn(List.of());

        List<TeamResponseDTO> result = teamService.verwijderTeam(1);

        verify(teamRepository, times(1)).deleteById(1);
        assertThat(result).isEmpty();
    }

    @Test
    void geefTeamsVanWerknemer_geeftCorrectTeamsTerug() {
        TeamwerknemerId twId = new TeamwerknemerId();
        twId.setTeamId(1);
        twId.setWerknemerId(1);
        Teamwerknemer tw = new Teamwerknemer();
        tw.setId(twId);
        tw.setWerknemer(werknemer);
        tw.setTeam(team);
        tw.setIsSupervisor(false);

        when(teamwerknemerRepository.findByWerknemerId(1)).thenReturn(List.of(tw));
        when(siteteamRepository.findByTeamId(1)).thenReturn(List.of());

        List<TeamResponseDTO> result = teamService.geefTeamsVanWerknemer(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).naam()).isEqualTo("Team Alpha");
    }

    private Werknemer maakWerknemer(int id) {
        Werknemer w = new Werknemer();
        w.setId(id);
        w.setNaam("Naam" + id);
        w.setVoornaam("Voornaam" + id);
        w.setEmail("w" + id + "@test.be");
        w.setTelefoonnummer("049900000" + id);
        w.setGeboortedatum(LocalDate.of(1990, 1, 1));
        w.setRol("Werknemer");
        w.setStatus("Actief");
        w.setWachtwoord("hash");
        return w;
    }
}

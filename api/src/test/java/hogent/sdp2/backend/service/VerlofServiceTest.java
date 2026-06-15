package hogent.sdp2.backend.service;

import hogent.sdp2.backend.auth.SessieService;
import hogent.sdp2.backend.domain.Team;
import hogent.sdp2.backend.domain.Teamwerknemer;
import hogent.sdp2.backend.domain.TeamwerknemerId;
import hogent.sdp2.backend.domain.Verlof;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.rest.dto.request.VerlofAanvragenDTO;
import hogent.sdp2.backend.rest.dto.response.GeschiedenisItemDTO;
import hogent.sdp2.backend.rest.repository.TeamwerknemerRepository;
import hogent.sdp2.backend.rest.repository.VerlofRepository;
import hogent.sdp2.backend.rest.repository.WerknemerRepository;
import hogent.sdp2.backend.rest.service.afwezigheid.VerlofService;
import hogent.sdp2.backend.rest.service.notificatie.NotificatieService;
import hogent.sdp2.backend.rest.service.sse.SseService;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerlofServiceTest {

    @Mock private VerlofRepository verlofRepository;
    @Mock private WerknemerRepository werknemerRepository;
    @Mock private TeamwerknemerRepository teamwerknemerRepository;
    @Mock private NotificatieService notificatieService;
    @Mock private SessieService sessieService;
    @Mock private SseService sseService;

    @InjectMocks private VerlofService verlofService;

    private Werknemer werknemer;
    private Werknemer manager;
    private Verlof verlof;
    private Team team;
    private Teamwerknemer teamwerknemerWerknemer;
    private Teamwerknemer teamwerknemerManager;

    @BeforeEach
    void setUp() {
        werknemer = new Werknemer();
        werknemer.setId(1);
        werknemer.setNaam("Janssen");
        werknemer.setVoornaam("Jan");
        werknemer.setRol("Werknemer");
        werknemer.setStatus("Actief");

        manager = new Werknemer();
        manager.setId(10);
        manager.setNaam("Manager");
        manager.setVoornaam("Marc");
        manager.setRol("Manager");

        team = new Team();
        team.setId(1);
        team.setNaam("Team A");
        team.setManager(manager);

        TeamwerknemerId twId = new TeamwerknemerId();
        twId.setTeamId(1);
        twId.setWerknemerId(1);
        teamwerknemerWerknemer = new Teamwerknemer();
        teamwerknemerWerknemer.setId(twId);
        teamwerknemerWerknemer.setTeam(team);
        teamwerknemerWerknemer.setWerknemer(werknemer);
        teamwerknemerWerknemer.setIsSupervisor(false);

        TeamwerknemerId mId = new TeamwerknemerId();
        mId.setTeamId(1);
        mId.setWerknemerId(10);
        teamwerknemerManager = new Teamwerknemer();
        teamwerknemerManager.setId(mId);
        teamwerknemerManager.setTeam(team);
        teamwerknemerManager.setWerknemer(manager);
        teamwerknemerManager.setIsSupervisor(false);

        verlof = new Verlof();
        verlof.setId(50);
        verlof.setWerknemer(werknemer);
        verlof.setStartDatum(LocalDate.of(2025, 6, 1));
        verlof.setEindDatum(LocalDate.of(2025, 6, 5));
        verlof.setType("Jaarlijks verlof");
        verlof.setStatus("In afwachting");
        verlof.setGoedkeurderId(10);
    }

    @Test
    void vraagVerlofAan_slaatVerlofOpEnStuurtNotificatie() {
        VerlofAanvragenDTO dto = new VerlofAanvragenDTO(
                1, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 5), "Jaarlijks verlof");

        when(werknemerRepository.findById(1)).thenReturn(Optional.of(werknemer));
        when(teamwerknemerRepository.findByWerknemerId(1)).thenReturn(List.of(teamwerknemerWerknemer));
        when(teamwerknemerRepository.findGoedkeurderVanTeam(1)).thenReturn(List.of(teamwerknemerManager));
        when(verlofRepository.save(any(Verlof.class))).thenAnswer(inv -> {
            Verlof v = inv.getArgument(0);
            v.setId(50);
            return v;
        });

        String result = verlofService.vraagVerlofAan(dto);

        assertThat(result).contains("succesvol");
        verify(verlofRepository, times(1)).save(any(Verlof.class));
        verify(notificatieService, times(1)).maakNotificatie(eq(10), anyString(), anyString(), any());
        verify(sseService, times(1)).pushEvent(eq(10), eq("verlof_aangevraagd"), any());
    }

    @Test
    void vraagVerlofAan_gooidExceptionAlsEindDatumVoorStartDatum() {
        VerlofAanvragenDTO dto = new VerlofAanvragenDTO(
                1, LocalDate.of(2025, 6, 5), LocalDate.of(2025, 6, 1), "Jaarlijks verlof");

        when(werknemerRepository.findById(1)).thenReturn(Optional.of(werknemer));

        assertThatThrownBy(() -> verlofService.vraagVerlofAan(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Einddatum mag niet voor startdatum");
    }

    @Test
    void vraagVerlofAan_gooidExceptionAlsGeenManagerGevonden() {
        VerlofAanvragenDTO dto = new VerlofAanvragenDTO(
                1, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 5), "Jaarlijks verlof");

        when(werknemerRepository.findById(1)).thenReturn(Optional.of(werknemer));
        when(teamwerknemerRepository.findByWerknemerId(1)).thenReturn(List.of());

        assertThatThrownBy(() -> verlofService.vraagVerlofAan(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Geen manager gevonden");
    }

    @Test
    void vraagVerlofAan_gooidExceptionBijOnbekendeWerknemer() {
        VerlofAanvragenDTO dto = new VerlofAanvragenDTO(
                999, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 5), "Jaarlijks verlof");

        when(werknemerRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> verlofService.vraagVerlofAan(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Werknemer niet gevonden");
    }

    @Test
    void geefVerlofStatus_geeftStatusTerug() {
        when(verlofRepository.findById(50)).thenReturn(Optional.of(verlof));

        String status = verlofService.geefVerlofStatus(50);

        assertThat(status).isEqualTo("In afwachting");
    }

    @Test
    void geefVerlofStatus_gooidExceptionBijOnbekendVerlof() {
        when(verlofRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> verlofService.geefVerlofStatus(999))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Verlof niet gevonden");
    }

    @Test
    void keurVerlofGoed_zetStatusOpGoedgekeurd() {
        when(verlofRepository.findById(50)).thenReturn(Optional.of(verlof));
        when(verlofRepository.save(any())).thenReturn(verlof);

        String result = verlofService.keurVerlofGoed(50);

        assertThat(verlof.getStatus()).isEqualTo("Goedgekeurd");
        assertThat(result).contains("goedgekeurd");
        verify(notificatieService).maakNotificatie(eq(1), anyString(), anyString(), eq(50));
        verify(sseService).pushEvent(eq(1), eq("verlof_goedgekeurd"), any());
    }

    @Test
    void keurVerlofGoed_gooidExceptionBijOnbekendVerlof() {
        when(verlofRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> verlofService.keurVerlofGoed(999))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Verlof niet gevonden");
    }

    @Test
    void wijsVerlofAf_zetStatusOpAfgewezen() {
        when(verlofRepository.findById(50)).thenReturn(Optional.of(verlof));
        when(verlofRepository.save(any())).thenReturn(verlof);

        String result = verlofService.wijsVerlofAf(50);

        assertThat(verlof.getStatus()).isEqualTo("Afgewezen");
        assertThat(result).contains("afgewezen");
        verify(notificatieService).maakNotificatie(eq(1), anyString(), anyString());
        verify(sseService).pushEvent(eq(1), eq("verlof_afgewezen"), any());
    }

    @Test
    void wijsVerlofAf_gooidExceptionBijOnbekendVerlof() {
        when(verlofRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> verlofService.wijsVerlofAf(999))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Verlof niet gevonden");
    }

    @Test
    void annuleerVerlof_zetStatusOpGeannuleerd() {
        when(verlofRepository.findById(50)).thenReturn(Optional.of(verlof));
        when(verlofRepository.save(any())).thenReturn(verlof);
        when(teamwerknemerRepository.findByWerknemerId(1)).thenReturn(List.of());

        String result = verlofService.annuleerVerlof(50);

        assertThat(verlof.getStatus()).isEqualTo("Geannuleerd");
        assertThat(result).contains("geannuleerd");
    }

    @Test
    void annuleerVerlof_gooidExceptionBijOnbekendVerlof() {
        when(verlofRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> verlofService.annuleerVerlof(999))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Verlof niet gevonden");
    }

    @Test
    void geefVerlofVanWerknemer_geeftHistoriekTerug() {
        when(verlofRepository.findByWerknemerId(1)).thenReturn(List.of(verlof));

        List<GeschiedenisItemDTO> result = verlofService.geefVerlofVanWerknemer(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).type()).isEqualTo("Verlof");
        assertThat(result.get(0).status()).isEqualTo("In afwachting");
        assertThat(result.get(0).omschrijving()).isEqualTo("Jaarlijks verlof");
    }

    @Test
    void geefVerlofVanWerknemer_geeftLegeListBijGeenVerlof() {
        when(verlofRepository.findByWerknemerId(99)).thenReturn(List.of());

        List<GeschiedenisItemDTO> result = verlofService.geefVerlofVanWerknemer(99);

        assertThat(result).isEmpty();
    }
}

package hogent.sdp2.backend.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import hogent.sdp2.backend.auth.SessieService;
import hogent.sdp2.backend.domain.Site;
import hogent.sdp2.backend.domain.Taken;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.rest.dto.request.TaakAanmakenDTO;
import hogent.sdp2.backend.rest.dto.request.TaakResponseDTO;
import hogent.sdp2.backend.rest.repository.SiteRepository;
import hogent.sdp2.backend.rest.repository.SiteteamRepository;
import hogent.sdp2.backend.rest.repository.TaakWerknemerRepository;
import hogent.sdp2.backend.rest.repository.TakenRepository;
import hogent.sdp2.backend.rest.repository.TeamwerknemerRepository;
import hogent.sdp2.backend.rest.repository.WerknemerRepository;
import hogent.sdp2.backend.rest.service.taken.TakenService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class TakenServiceTest {

    @Mock private TakenRepository takenRepository;
    @Mock private WerknemerRepository werknemerRepository;
    @Mock private TeamwerknemerRepository teamwerknemerRepository;
    @Mock private SiteteamRepository siteteamRepository;

    // DE FIX 1: De missende repositories toevoegen aan de testomgeving
    @Mock private TaakWerknemerRepository taakWerknemerRepository;
    @Mock private SiteRepository siteRepository;

    @InjectMocks private TakenService takenService;

    private Werknemer werknemer;
    private Taken taak;
    private Site site;

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
        werknemer.setWachtwoord("hashed");

        taak = new Taken();
        taak.setId(10);
        taak.setWerknemer(werknemer);
        taak.setTitel("Test taak");
        taak.setBeschrijving("Beschrijving");
        taak.setAfgewerkt("nee");
        taak.setDeadline(LocalDate.of(2025, 12, 31));

        site = new Site();
        site.setId(1);
        site.setNaam("Delaware Kantoor");
    }

    @Test
    void geefTakenVanWerknemer_geeftLijstTerug() {
        when(takenRepository.findByWerknemer_Id(1)).thenReturn(List.of(taak));
        // DE FIX 2: Vertel de mock wat hij moet doen als DTO wordt opgebouwd
        when(taakWerknemerRepository.findByIdTaakId(anyInt())).thenReturn(List.of());

        List<TaakResponseDTO> result = takenService.geefTakenVanWerknemer(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).titel()).isEqualTo("Test taak");
    }

    @Test
    void geefTakenVanWerknemer_geeftLegeListBijGeenTaken() {
        when(takenRepository.findByWerknemer_Id(99)).thenReturn(List.of());

        List<TaakResponseDTO> result = takenService.geefTakenVanWerknemer(99);

        assertThat(result).isEmpty();
    }

    @Test
    void maakTaakAan_slaatTaakOp() {
        TaakAanmakenDTO dto =
            new TaakAanmakenDTO(1, "Nieuwe taak", "Omschrijving", LocalDate.of(2025, 6, 1), 1, "09:00", "17:00");
        when(werknemerRepository.findById(1)).thenReturn(Optional.of(werknemer));

        // DE FIX 3: Vertel de site mock dat hij de dummy site moet teruggeven
        when(siteRepository.findById(1)).thenReturn(Optional.of(site));

        when(takenRepository.save(any(Taken.class))).thenReturn(taak);

        String result = takenService.maakTaakAan(dto);

        assertThat(result).contains("Nieuwe taak");
        verify(takenRepository, times(1)).save(any(Taken.class));
    }

    @Test
    void maakTaakAan_gooidExceptionBijOnbekendeWerknemer() {
        TaakAanmakenDTO dto =
            new TaakAanmakenDTO(999, "Taak", "Omschrijving", LocalDate.of(2025, 6, 1), 1, "09:00", "17:00");
        when(werknemerRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> takenService.maakTaakAan(dto))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Werknemer niet gevonden");
    }

    @Test
    void markeerAfgewerkt_zetStatusOpJa() {
        when(takenRepository.findById(10)).thenReturn(Optional.of(taak));
        when(takenRepository.save(any(Taken.class))).thenAnswer(inv -> inv.getArgument(0));

        String result = takenService.markeerAfgewerkt(10);

        assertThat(taak.getAfgewerkt()).isEqualTo("ja");
        assertThat(result).contains("afgewerkt");
    }

    @Test
    void markeerAfgewerkt_gooidExceptionBijOnbekendeTaak() {
        when(takenRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> takenService.markeerAfgewerkt(999))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Taak niet gevonden");
    }

    @Test
    void geefAlleTaken_geeftAlleTakenTerug() {
        when(takenRepository.findAll()).thenReturn(List.of(taak));
        // DE FIX 4: Zelfde mock nodig voor de DTO omzetting!
        when(taakWerknemerRepository.findByIdTaakId(anyInt())).thenReturn(List.of());

        List<TaakResponseDTO> result = takenService.geefAlleTaken();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).beschrijving()).isEqualTo("Beschrijving");
    }

    @Test
    void wijsTaakToe_wijstCorrectToe() {
        Werknemer nieuweWerknemer = new Werknemer();
        nieuweWerknemer.setId(2);
        nieuweWerknemer.setVoornaam("Piet");
        nieuweWerknemer.setNaam("Pieters");

        when(takenRepository.findById(10)).thenReturn(Optional.of(taak));
        when(werknemerRepository.findById(2)).thenReturn(Optional.of(nieuweWerknemer));
        when(takenRepository.save(any(Taken.class))).thenReturn(taak);

        String result = takenService.wijsTaakToe(10, 2);

        assertThat(result).contains("Piet");
        assertThat(taak.getWerknemer()).isEqualTo(nieuweWerknemer);
    }

    @Test
    void wijsTaakToe_gooidExceptionBijOnbekendeTaak() {
        when(takenRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> takenService.wijsTaakToe(999, 1))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Taak niet gevonden");
    }

    @Test
    void wijsTaakToe_gooidExceptionBijOnbekendeWerknemer() {
        when(takenRepository.findById(10)).thenReturn(Optional.of(taak));
        when(werknemerRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> takenService.wijsTaakToe(10, 999))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Werknemer niet gevonden");
    }

    @Test
    void verwijderTaak_verwijdertCorrect() {
        when(takenRepository.findById(10)).thenReturn(Optional.of(taak));

        String result = takenService.verwijderTaak(10);

        assertThat(result).contains("Test taak");
        verify(takenRepository, times(1)).delete(taak);
    }

    @Test
    void verwijderTaak_gooidExceptionBijOnbekendeTaak() {
        when(takenRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> takenService.verwijderTaak(999))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Taak niet gevonden");
    }

    @Test
    void assertEigenaarVanTaak_lootDoorAlsAdminOfManager() {
        SessieService sessieService = mock(SessieService.class);
        when(sessieService.isAdminOfManager()).thenReturn(true);

        assertThatNoException()
            .isThrownBy(() -> takenService.assertEigenaarVanTaak(10, sessieService));

        verify(takenRepository, never()).findById(any());
    }

    @Test
    void assertEigenaarVanTaak_gooidExceptionAlsNietEigenaar() {
        SessieService sessieService = mock(SessieService.class);
        when(sessieService.isAdminOfManager()).thenReturn(false);
        when(sessieService.getIngelogdeWerknemerId()).thenReturn(99);
        when(takenRepository.findById(10)).thenReturn(Optional.of(taak));

        assertThatThrownBy(() -> takenService.assertEigenaarVanTaak(10, sessieService))
            .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void assertEigenaarVanTaak_lootDoorAlsEigenaar() {
        SessieService sessieService = mock(SessieService.class);
        when(sessieService.isAdminOfManager()).thenReturn(false);
        when(sessieService.getIngelogdeWerknemerId()).thenReturn(1);
        when(takenRepository.findById(10)).thenReturn(Optional.of(taak));

        assertThatNoException()
            .isThrownBy(() -> takenService.assertEigenaarVanTaak(10, sessieService));
    }
}

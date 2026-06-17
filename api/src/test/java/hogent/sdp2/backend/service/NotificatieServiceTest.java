package hogent.sdp2.backend.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import hogent.sdp2.backend.domain.Notificatie;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.rest.dto.request.NotificatieDTO;
import hogent.sdp2.backend.rest.repository.NotificatieRepository;
import hogent.sdp2.backend.rest.repository.WerknemerRepository;
import hogent.sdp2.backend.rest.service.notificatie.NotificatieService;
import hogent.sdp2.backend.rest.service.sse.SseService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificatieServiceTest {

    @Mock private NotificatieRepository notificatieRepository;
    @Mock private WerknemerRepository werknemerRepository;
    @Mock private SseService sseService;

    @InjectMocks private NotificatieService notificatieService;

    private Werknemer werknemer;
    private Notificatie notificatie;

    @BeforeEach
    void setUp() {
        werknemer = new Werknemer();
        werknemer.setId(1);
        werknemer.setNaam("Janssen");
        werknemer.setVoornaam("Jan");
        werknemer.setEmail("jan@test.be");

        notificatie = new Notificatie();
        notificatie.setId(100);
        notificatie.setWerknemer(werknemer);
        notificatie.setTitel("Test titel");
        notificatie.setBericht("Test bericht");
        notificatie.setGelezen("Nee");
        notificatie.setDatum(LocalDate.now());
        notificatie.setReferentieId(null);
    }

    @Test
    void maakNotificatie_slaatOpEnPushSSE() {
        when(werknemerRepository.findById(1)).thenReturn(Optional.of(werknemer));
        when(notificatieRepository.save(any(Notificatie.class)))
                .thenAnswer(
                        inv -> {
                            Notificatie n = inv.getArgument(0);
                            n.setId(100);
                            return n;
                        });

        notificatieService.maakNotificatie(1, "Titel", "Bericht");

        verify(notificatieRepository, times(1)).save(any(Notificatie.class));
        verify(sseService, times(1)).pushEvent(eq(1), eq("nieuwe_notificatie"), any());
    }

    @Test
    void maakNotificatie_zetCorrectVelden() {
        when(werknemerRepository.findById(1)).thenReturn(Optional.of(werknemer));
        when(notificatieRepository.save(any(Notificatie.class)))
                .thenAnswer(
                        inv -> {
                            Notificatie n = inv.getArgument(0);
                            n.setId(99);
                            return n;
                        });

        notificatieService.maakNotificatie(1, "Mijn Titel", "Mijn Bericht");

        ArgumentCaptor<Notificatie> captor = ArgumentCaptor.forClass(Notificatie.class);
        verify(notificatieRepository).save(captor.capture());
        Notificatie opgeslagen = captor.getValue();

        assertThat(opgeslagen.getTitel()).isEqualTo("Mijn Titel");
        assertThat(opgeslagen.getBericht()).isEqualTo("Mijn Bericht");
        assertThat(opgeslagen.getGelezen()).isEqualTo("Nee");
        assertThat(opgeslagen.getDatum()).isEqualTo(LocalDate.now());
        assertThat(opgeslagen.getReferentieId()).isNull();
    }

    @Test
    void maakNotificatie_metReferentieId_slaatReferentieOp() {
        when(werknemerRepository.findById(1)).thenReturn(Optional.of(werknemer));
        when(notificatieRepository.save(any(Notificatie.class)))
                .thenAnswer(
                        inv -> {
                            Notificatie n = inv.getArgument(0);
                            n.setId(100);
                            return n;
                        });

        notificatieService.maakNotificatie(1, "Verlof", "Bericht", 42);

        ArgumentCaptor<Notificatie> captor = ArgumentCaptor.forClass(Notificatie.class);
        verify(notificatieRepository).save(captor.capture());
        assertThat(captor.getValue().getReferentieId()).isEqualTo(42);
    }

    @Test
    void maakNotificatie_gooidExceptionBijOnbekendeWerknemer() {
        when(werknemerRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificatieService.maakNotificatie(999, "T", "B"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Werknemer niet gevonden");
    }

    @Test
    void geefNotificatiesVanWerknemer_geeftLijstTerug() {
        when(notificatieRepository.findByWerknemerIdOrderByDatumDesc(1))
                .thenReturn(List.of(notificatie));

        List<NotificatieDTO> result = notificatieService.geefNotificatiesVanWerknemer(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).titel()).isEqualTo("Test titel");
        assertThat(result.get(0).bericht()).isEqualTo("Test bericht");
        assertThat(result.get(0).gelezen()).isEqualTo("Nee");
    }

    @Test
    void geefNotificatiesVanWerknemer_geeftLegeListBijGeenNotificaties() {
        when(notificatieRepository.findByWerknemerIdOrderByDatumDesc(1)).thenReturn(List.of());

        List<NotificatieDTO> result = notificatieService.geefNotificatiesVanWerknemer(1);

        assertThat(result).isEmpty();
    }

    @Test
    void geefAantalOngelezenNotificaties_geeftCorrectAantal() {
        when(notificatieRepository.countByWerknemerIdAndGelezen(1, "Nee")).thenReturn(5L);

        long result = notificatieService.geefAantalOngelezenNotificaties(1);

        assertThat(result).isEqualTo(5L);
    }

    @Test
    void geefAantalOngelezenNotificaties_geeftNulBijGeenOngelezen() {
        when(notificatieRepository.countByWerknemerIdAndGelezen(1, "Nee")).thenReturn(0L);

        long result = notificatieService.geefAantalOngelezenNotificaties(1);

        assertThat(result).isZero();
    }

    @Test
    void markeerAlsGelezen_zetGelezenOpJa() {
        when(notificatieRepository.findById(100)).thenReturn(Optional.of(notificatie));
        when(notificatieRepository.save(any())).thenReturn(notificatie);

        String result = notificatieService.markeerAlsGelezen(100);

        assertThat(notificatie.getGelezen()).isEqualTo("Ja");
        assertThat(result).contains("gelezen");
    }

    @Test
    void markeerAlsGelezen_gooidExceptionBijOnbekendeNotificatie() {
        when(notificatieRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificatieService.markeerAlsGelezen(999))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Notificatie niet gevonden");
    }

    @Test
    void verwijderNotificatie_roeptDeleteAan() {
        String result = notificatieService.verwijderNotificatie(100);

        verify(notificatieRepository, times(1)).deleteById(100);
        assertThat(result).contains("verwijderd");
    }
}

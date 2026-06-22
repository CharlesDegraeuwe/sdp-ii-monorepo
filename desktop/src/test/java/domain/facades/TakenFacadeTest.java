package domain.facades;

import domain.auth.Sessie;
import domain.dto.TaakDTO;
import domain.dto.WerknemerDTO;
import domain.services.TakenApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TakenFacadeTest {

    @Mock
    private TakenApiService api;

    @InjectMocks
    private TakenFacade facade;

    private WerknemerDTO werknemer;

    @BeforeEach
    void setUp() {
        werknemer = new WerknemerDTO(5, "Peeters", "Piet", "piet@test.be", "0471000000",
            LocalDate.of(1985, 6, 15), "Werknemer", "actief");
        Sessie.getInstance().setIngelogdeWerknemer(werknemer);
    }

    @Test
    void geefAlleTaken_retourneertLijst() {
        List<TaakDTO> taken = List.of();
        when(api.geefAlleTaken()).thenReturn(taken);

        List<TaakDTO> result = facade.geefAlleTaken();

        assertEquals(taken, result);
        verify(api).geefAlleTaken();
    }

    @Test
    void geefEigenTaken_gebruiktIngelogdeWerknemer() {
        List<TaakDTO> taken = List.of();
        when(api.geefTakenVanWerknemer(5)).thenReturn(taken);

        List<TaakDTO> result = facade.geefEigenTaken();

        assertEquals(taken, result);
        verify(api).geefTakenVanWerknemer(5);
    }

    @Test
    void maakTaakAan_metGeldigeData_roeptApiAan() {
        LocalDate deadline = LocalDate.now().plusDays(5);
        // FIX: 2 extra parameters toegevoegd voor uren
        when(api.maakTaakAan( "Titel", "Beschrijving", deadline, 1, "09:00", "17:00")).thenReturn("ok");

        String result = facade.maakTaakAan("Titel", "Beschrijving", deadline, 1, "09:00", "17:00");

        assertEquals("ok", result);
        verify(api).maakTaakAan( "Titel", "Beschrijving", deadline, 1, "09:00", "17:00");
    }

    @Test
    void maakTaakAan_metLegeTitel_gooit_IllegalArgumentException() {
        // FIX: 2 extra parameters toegevoegd
        assertThrows(IllegalArgumentException.class,
            () -> facade.maakTaakAan("", "Beschrijving", LocalDate.now().plusDays(1), 1, "09:00", "17:00"));
        verifyNoInteractions(api);
    }

    @Test
    void maakTaakAan_metNullTitel_gooit_IllegalArgumentException() {
        // FIX: 2 extra parameters toegevoegd
        assertThrows(IllegalArgumentException.class,
            () -> facade.maakTaakAan(null, "Beschrijving", LocalDate.now().plusDays(1), 1, "09:00", "17:00"));
        verifyNoInteractions(api);
    }

    @Test
    void maakTaakAan_metLegeBeschrijving_gooit_IllegalArgumentException() {
        // FIX: 2 extra parameters toegevoegd
        assertThrows(IllegalArgumentException.class,
            () -> facade.maakTaakAan("Titel", "", LocalDate.now().plusDays(1), 1, "09:00", "17:00"));
        verifyNoInteractions(api);
    }

    @Test
    void maakTaakAan_metNullDeadline_gooit_IllegalArgumentException() {
        // FIX: 2 extra parameters toegevoegd
        assertThrows(IllegalArgumentException.class,
            () -> facade.maakTaakAan("Titel", "Beschrijving", null, 1, "09:00", "17:00"));
        verifyNoInteractions(api);
    }

    @Test
    void maakTaakAan_metDeadlineInHetVerleden_gooit_IllegalArgumentException() {
        // FIX: 2 extra parameters toegevoegd
        assertThrows(IllegalArgumentException.class,
            () -> facade.maakTaakAan("Titel", "Beschrijving", LocalDate.now().minusDays(1), 1, "09:00", "17:00"));
        verifyNoInteractions(api);
    }

    @Test
    void maakTaakAan_metOngeldigSiteId_gooit_IllegalArgumentException() {
        // FIX: 2 extra parameters toegevoegd
        assertThrows(IllegalArgumentException.class,
            () -> facade.maakTaakAan("Titel", "Beschrijving", LocalDate.now().plusDays(1), 0, "09:00", "17:00"));
        verifyNoInteractions(api);
    }

    @Test
    void updateTaakToewijzingen_roeptApiAan() {
        List<Integer> werknemerIds = List.of(5);

        when(api.updateTaakToewijzingen(10, werknemerIds)).thenReturn("ok");

        String result = facade.updateTaakToewijzingen(10, werknemerIds);

        assertEquals("ok", result);
        verify(api).updateTaakToewijzingen(10, werknemerIds);
    }

    @Test
    void wijzigTaak_roeptApiAan() {
        when(api.wijzigTaak(10, "NieuweNaam", "NieuweOms")).thenReturn("ok");

        String result = facade.wijzigTaak(10, "NieuweNaam", "NieuweOms");

        assertEquals("ok", result);
        verify(api).wijzigTaak(10, "NieuweNaam", "NieuweOms");
    }

    @Test
    void verwijderTaak_roeptApiAan() {
        when(api.verwijderTaak(10)).thenReturn("ok");

        String result = facade.verwijderTaak(10);

        assertEquals("ok", result);
        verify(api).verwijderTaak(10);
    }

    @Test
    void markeerAfgewerkt_roeptApiAan() {
        when(api.markeerAfgewerkt(10)).thenReturn("ok");

        String result = facade.markeerAfgewerkt(10);

        assertEquals("ok", result);
        verify(api).markeerAfgewerkt(10);
    }
}

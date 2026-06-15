package domain.facades;

import domain.dto.WerknemerDTO;
import domain.services.WerknemersApiService;
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
class WerknemersFacadeTest {

    @Mock
    private WerknemersApiService api;

    @InjectMocks
    private WerknemersFacade facade;

    private static final String GELDIGE_GEBOORTEDATUM = LocalDate.now().minusYears(25).toString();

    @Test
    void geefAlleWerknemers_retourneertLijst() {
        List<WerknemerDTO> werknemers = List.of();
        when(api.getAlleWerknemers()).thenReturn(werknemers);

        List<WerknemerDTO> result = facade.geefAlleWerknemers();

        assertEquals(werknemers, result);
        verify(api).getAlleWerknemers();
    }

    @Test
    void zoekOpEmail_delegeertNaarApi() {
        WerknemerDTO w = new WerknemerDTO(1, "Jansen", "Jan", "jan@test.be", "0470000000",
                LocalDate.of(1990, 1, 1), "Werknemer", "actief");
        when(api.zoekOpEmail("jan@test.be")).thenReturn(w);

        WerknemerDTO result = facade.zoekOpEmail("jan@test.be");

        assertEquals(w, result);
    }

    @Test
    void zoekOpId_delegeertNaarApi() {
        WerknemerDTO w = new WerknemerDTO(1, "Jansen", "Jan", "jan@test.be", "0470000000",
                LocalDate.of(1990, 1, 1), "Werknemer", "actief");
        when(api.zoekOpId(1)).thenReturn(w);

        WerknemerDTO result = facade.zoekOpId(1);

        assertEquals(w, result);
    }

    @Test
    void registreerWerknemer_metGeldigeData_retourneertTrue() {
        when(api.registreerWerknemer(any(), any(), any(), any(), any(), any())).thenReturn(true);

        boolean result = facade.registreerWerknemer(
                "Jansen", "Jan", "jan@test.be", "0470123456", GELDIGE_GEBOORTEDATUM, "Werknemer");

        assertTrue(result);
        verify(api).registreerWerknemer(any(), any(), any(), any(), any(), any());
    }

    @Test
    void registreerWerknemer_metLegeNaam_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                facade.registreerWerknemer("", "Jan", "jan@test.be", "0470123456", GELDIGE_GEBOORTEDATUM, "Werknemer"));
        verifyNoInteractions(api);
    }

    @Test
    void registreerWerknemer_metTeKorteNaam_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                facade.registreerWerknemer("A", "Jan", "jan@test.be", "0470123456", GELDIGE_GEBOORTEDATUM, "Werknemer"));
        verifyNoInteractions(api);
    }

    @Test
    void registreerWerknemer_metLegeVoornaam_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                facade.registreerWerknemer("Jansen", "", "jan@test.be", "0470123456", GELDIGE_GEBOORTEDATUM, "Werknemer"));
        verifyNoInteractions(api);
    }

    @Test
    void registreerWerknemer_metOngeldigEmail_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                facade.registreerWerknemer("Jansen", "Jan", "geen-email", "0470123456", GELDIGE_GEBOORTEDATUM, "Werknemer"));
        verifyNoInteractions(api);
    }

    @Test
    void registreerWerknemer_metOngeldigTelefoon_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                facade.registreerWerknemer("Jansen", "Jan", "jan@test.be", "123", GELDIGE_GEBOORTEDATUM, "Werknemer"));
        verifyNoInteractions(api);
    }

    @Test
    void registreerWerknemer_metJongerDan16Jaar_gooit_IllegalArgumentException() {
        String tienJaarGeleden = LocalDate.now().minusYears(10).toString();
        assertThrows(IllegalArgumentException.class, () ->
                facade.registreerWerknemer("Jansen", "Jan", "jan@test.be", "0470123456", tienJaarGeleden, "Werknemer"));
        verifyNoInteractions(api);
    }

    @Test
    void registreerWerknemer_metLegeRol_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                facade.registreerWerknemer("Jansen", "Jan", "jan@test.be", "0470123456", GELDIGE_GEBOORTEDATUM, ""));
        verifyNoInteractions(api);
    }

    @Test
    void veranderStatus_delegeertNaarApi() {
        when(api.veranderStatus(1, "deactiveer")).thenReturn(true);

        boolean result = facade.veranderStatus(1, "deactiveer");

        assertTrue(result);
        verify(api).veranderStatus(1, "deactiveer");
    }
}

package domain.facades;

import domain.dto.NotificatieDTO;
import domain.services.NotificatieApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificatieFacadeTest {

    @Mock
    private NotificatieApiService api;

    @InjectMocks
    private NotificatieFacade facade;

    @Test
    void geefNotificaties_retourneertLijst() {
        List<NotificatieDTO> notificaties = List.of();
        when(api.geefNotificaties(5)).thenReturn(notificaties);

        List<NotificatieDTO> result = facade.geefNotificaties(5);

        assertEquals(notificaties, result);
        verify(api).geefNotificaties(5);
    }

    @Test
    void geefAantalOngelezen_retourneertAantal() {
        when(api.geefAantalOngelezen(5)).thenReturn(3L);

        long result = facade.geefAantalOngelezen(5);

        assertEquals(3L, result);
        verify(api).geefAantalOngelezen(5);
    }

    @Test
    void geefAantalOngelezen_retourneertNulAlsAllesGelezen() {
        when(api.geefAantalOngelezen(5)).thenReturn(0L);

        long result = facade.geefAantalOngelezen(5);

        assertEquals(0L, result);
    }

    @Test
    void markeerAlsGelezen_delegeertNaarApi() {
        when(api.markeerAlsGelezen(42)).thenReturn("gelezen");

        String result = facade.markeerAlsGelezen(42);

        assertEquals("gelezen", result);
        verify(api).markeerAlsGelezen(42);
    }

    @Test
    void verwijderNotificatie_delegeertNaarApi() {
        when(api.verwijderNotificatie(42)).thenReturn("verwijderd");

        String result = facade.verwijderNotificatie(42);

        assertEquals("verwijderd", result);
        verify(api).verwijderNotificatie(42);
    }
}

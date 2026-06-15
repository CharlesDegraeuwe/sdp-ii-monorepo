package domain.facades;

import domain.services.AfwezigheidApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AfwezigheidFacadeTest {

    @Mock
    private AfwezigheidApiService api;

    @InjectMocks
    private AfwezigheidFacade facade;

    @Test
    void meldAfwezigheid_metGeldigeData_roeptApiAan() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate eind = LocalDate.now().plusDays(3);
        when(api.meldAfwezigheid(any())).thenReturn("gemeld");

        String result = facade.meldAfwezigheid(1, start, eind, "griep", null);

        assertEquals("gemeld", result);
        verify(api).meldAfwezigheid(any());
    }

    @Test
    void meldAfwezigheid_metNullStartDatum_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> facade.meldAfwezigheid(1, null, LocalDate.now().plusDays(3), "griep", null));
        verifyNoInteractions(api);
    }

    @Test
    void meldAfwezigheid_metNullEindDatum_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> facade.meldAfwezigheid(1, LocalDate.now(), null, "griep", null));
        verifyNoInteractions(api);
    }

    @Test
    void meldAfwezigheid_eindDatumVoorStartDatum_gooit_IllegalArgumentException() {
        LocalDate start = LocalDate.now().plusDays(5);
        LocalDate eind = LocalDate.now().plusDays(2);
        assertThrows(IllegalArgumentException.class,
                () -> facade.meldAfwezigheid(1, start, eind, "griep", null));
        verifyNoInteractions(api);
    }

    @Test
    void meldAfwezigheid_metLegeReden_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> facade.meldAfwezigheid(1, LocalDate.now(), LocalDate.now().plusDays(2), "", null));
        verifyNoInteractions(api);
    }

    @Test
    void meldAfwezigheid_metNullReden_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> facade.meldAfwezigheid(1, LocalDate.now(), LocalDate.now().plusDays(2), null, null));
        verifyNoInteractions(api);
    }

    @Test
    void meldAfwezigheid_metCertificaat_stuurtCertificaatMee() {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate eind = LocalDate.now().plusDays(3);
        byte[] certificaat = new byte[]{1, 2, 3};
        when(api.meldAfwezigheid(any())).thenReturn("gemeld");

        String result = facade.meldAfwezigheid(1, start, eind, "operatie", certificaat);

        assertEquals("gemeld", result);
        verify(api).meldAfwezigheid(any());
    }
}

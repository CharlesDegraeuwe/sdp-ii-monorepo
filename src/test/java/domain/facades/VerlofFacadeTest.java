package domain.facades;

import domain.services.VerlofApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerlofFacadeTest {

    @Mock
    private VerlofApiService api;

    @InjectMocks
    private VerlofFacade facade;

    @Test
    void vraagVerlofAan_metGeldigeData_roeptApiAan() {
        LocalDate start = LocalDate.now().plusDays(5);
        LocalDate eind = LocalDate.now().plusDays(10);
        when(api.vraagVerlofAan(any())).thenReturn("aangevraagd");

        String result = facade.vraagVerlofAan(1, start, eind, "jaarlijks");

        assertEquals("aangevraagd", result);
        verify(api).vraagVerlofAan(any());
    }

    @Test
    void vraagVerlofAan_metNullStartDatum_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> facade.vraagVerlofAan(1, null, LocalDate.now().plusDays(5), "jaarlijks"));
        verifyNoInteractions(api);
    }

    @Test
    void vraagVerlofAan_metNullEindDatum_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> facade.vraagVerlofAan(1, LocalDate.now().plusDays(1), null, "jaarlijks"));
        verifyNoInteractions(api);
    }

    @Test
    void vraagVerlofAan_eindDatumVoorStartDatum_gooit_IllegalArgumentException() {
        LocalDate start = LocalDate.now().plusDays(5);
        LocalDate eind = LocalDate.now().plusDays(2);
        assertThrows(IllegalArgumentException.class,
                () -> facade.vraagVerlofAan(1, start, eind, "jaarlijks"));
        verifyNoInteractions(api);
    }

    @Test
    void vraagVerlofAan_startDatumInHetVerleden_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> facade.vraagVerlofAan(1, LocalDate.now().minusDays(1),
                        LocalDate.now().plusDays(5), "jaarlijks"));
        verifyNoInteractions(api);
    }

    @Test
    void vraagVerlofAan_metLeegType_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> facade.vraagVerlofAan(1, LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(5), ""));
        verifyNoInteractions(api);
    }

    @Test
    void keurVerlofGoed_roeptApiAan() {
        when(api.keurVerlofGoed(42)).thenReturn("goedgekeurd");

        String result = facade.keurVerlofGoed(42);

        assertEquals("goedgekeurd", result);
        verify(api).keurVerlofGoed(42);
    }

    @Test
    void wijsVerlofAf_roeptApiAan() {
        when(api.wijsVerlofAf(42)).thenReturn("afgewezen");

        String result = facade.wijsVerlofAf(42);

        assertEquals("afgewezen", result);
        verify(api).wijsVerlofAf(42);
    }

    @Test
    void annuleerVerlof_roeptApiAan() {
        when(api.annuleerVerlof(42)).thenReturn("geannuleerd");

        String result = facade.annuleerVerlof(42);

        assertEquals("geannuleerd", result);
        verify(api).annuleerVerlof(42);
    }
}

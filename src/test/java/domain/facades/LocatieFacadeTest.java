package domain.facades;

import domain.dto.LocatieDTO;
import domain.dto.MachineAanmaakDTO;
import domain.services.LocatieApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocatieFacadeTest {

    @Mock
    private LocatieApiService api;

    @InjectMocks
    private LocatieFacade facade;

    private LocatieDTO geldigeLocatie() {
        return new LocatieDTO(1, "Hoofdkantoor", "Gent", 50, "actief");
    }

    @Test
    void geefAlleLocaties_retourneertLijst() {
        List<LocatieDTO> locaties = List.of(geldigeLocatie());
        when(api.geefAlleLocaties()).thenReturn(locaties);

        List<LocatieDTO> result = facade.geefAlleLocaties();

        assertEquals(locaties, result);
        verify(api).geefAlleLocaties();
    }

    @Test
    void vindLocatie_delegeertNaarApi() {
        when(api.vindLocatie(1)).thenReturn(geldigeLocatie());

        LocatieDTO result = facade.vindLocatie(1);

        assertNotNull(result);
        verify(api).vindLocatie(1);
    }

    @Test
    void verwijderLocatie_delegeertNaarApi() {
        when(api.verwijderLocatie(1)).thenReturn(true);

        boolean result = facade.verwijderLocatie(1);

        assertTrue(result);
        verify(api).verwijderLocatie(1);
    }

    @Test
    void maakLocatie_metGeldigeData_roeptApiAan() {
        when(api.maakLocatie(any())).thenReturn(true);

        boolean result = facade.maakLocatie(geldigeLocatie());

        assertTrue(result);
        verify(api).maakLocatie(any());
    }

    @Test
    void maakLocatie_metLegeNaam_gooit_IllegalArgumentException() {
        LocatieDTO ongeldig = new LocatieDTO(null, "", "Gent", 50, "actief");
        assertThrows(IllegalArgumentException.class, () -> facade.maakLocatie(ongeldig));
        verifyNoInteractions(api);
    }

    @Test
    void maakLocatie_metLeegAdres_gooit_IllegalArgumentException() {
        LocatieDTO ongeldig = new LocatieDTO(null, "Naam", "", 50, "actief");
        assertThrows(IllegalArgumentException.class, () -> facade.maakLocatie(ongeldig));
        verifyNoInteractions(api);
    }

    @Test
    void maakLocatie_metNulCapaciteit_gooit_IllegalArgumentException() {
        LocatieDTO ongeldig = new LocatieDTO(null, "Naam", "Gent", 0, "actief");
        assertThrows(IllegalArgumentException.class, () -> facade.maakLocatie(ongeldig));
        verifyNoInteractions(api);
    }

    @Test
    void maakLocatie_metNegatieveCapaciteit_gooit_IllegalArgumentException() {
        LocatieDTO ongeldig = new LocatieDTO(null, "Naam", "Gent", -5, "actief");
        assertThrows(IllegalArgumentException.class, () -> facade.maakLocatie(ongeldig));
        verifyNoInteractions(api);
    }

    @Test
    void maakLocatie_metLegeStatus_gooit_IllegalArgumentException() {
        LocatieDTO ongeldig = new LocatieDTO(null, "Naam", "Gent", 50, "");
        assertThrows(IllegalArgumentException.class, () -> facade.maakLocatie(ongeldig));
        verifyNoInteractions(api);
    }

    @Test
    void wijzigLocatie_metGeldigeData_roeptApiAan() {
        when(api.wijzigLocatie(eq(1), any())).thenReturn(true);

        boolean result = facade.wijzigLocatie(1, geldigeLocatie());

        assertTrue(result);
        verify(api).wijzigLocatie(eq(1), any());
    }

    @Test
    void maakMachine_metGeldigeData_roeptApiAan() {
        MachineAanmaakDTO dto = new MachineAanmaakDTO("Persmachine", "actief", 1);
        when(api.maakMachine(dto)).thenReturn(true);

        boolean result = facade.maakMachine(dto);

        assertTrue(result);
        verify(api).maakMachine(dto);
    }

    @Test
    void maakMachine_metLegeNaam_gooit_IllegalArgumentException() {
        MachineAanmaakDTO dto = new MachineAanmaakDTO("", "actief", 1);
        assertThrows(IllegalArgumentException.class, () -> facade.maakMachine(dto));
        verifyNoInteractions(api);
    }

    @Test
    void maakMachine_metLegeStatus_gooit_IllegalArgumentException() {
        MachineAanmaakDTO dto = new MachineAanmaakDTO("Persmachine", "", 1);
        assertThrows(IllegalArgumentException.class, () -> facade.maakMachine(dto));
        verifyNoInteractions(api);
    }

    @Test
    void haalMachinesOpVoorSite_delegeertNaarApi() {
        when(api.haalMachinesOpVoorSite(1)).thenReturn(List.of());

        List<MachineAanmaakDTO> result = facade.haalMachinesOpVoorSite(1);

        assertNotNull(result);
        verify(api).haalMachinesOpVoorSite(1);
    }

    @Test
    void verwijderMachine_delegeertNaarApi() {
        when(api.verwijderMachine(5)).thenReturn(true);

        boolean result = facade.verwijderMachine(5);

        assertTrue(result);
        verify(api).verwijderMachine(5);
    }
}

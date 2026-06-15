package domain.facades;

import domain.dto.LogDTO;
import domain.services.LogsApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogFacadeTest {

    @Mock
    private LogsApiService api;

    @InjectMocks
    private LogFacade facade;

    private LogDTO maakLogDTO() {
        return new LogDTO(1, 5, "CREATE", "taken", LocalDateTime.now(), "Taak aangemaakt");
    }

    @Test
    void geefAlleLogs_retourneertLijst() {
        List<LogDTO> logs = List.of(maakLogDTO());
        when(api.getAlleLogs()).thenReturn(logs);

        List<LogDTO> result = facade.geefAlleLogs();

        assertEquals(logs, result);
        verify(api).getAlleLogs();
    }

    @Test
    void geefAlleLogs_retourneertLeegeLijstAlsErGeenLogsZijn() {
        when(api.getAlleLogs()).thenReturn(List.of());

        List<LogDTO> result = facade.geefAlleLogs();

        assertTrue(result.isEmpty());
    }

    @Test
    void zoekOpId_retourneertJuisteLog() {
        LogDTO log = maakLogDTO();
        when(api.zoekOpId(1)).thenReturn(log);

        LogDTO result = facade.zoekOpId(1);

        assertEquals(log, result);
        verify(api).zoekOpId(1);
    }

    @Test
    void voegLogToe_delegeertNaarApi() {
        LogDTO log = maakLogDTO();

        facade.voegLogToe(log);

        verify(api).voegLogToe(log);
    }
}

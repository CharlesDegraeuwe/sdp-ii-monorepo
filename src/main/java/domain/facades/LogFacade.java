package domain.facades;

import domain.dto.LogDTO;
import domain.dto.UpdateWerknemerDTO;
import domain.dto.WerknemerDTO;
import domain.services.LogsApiService;
import domain.services.WerknemersApiService;

import java.util.List;

public class LogFacade {
    private LogsApiService api = new LogsApiService();

    public List<LogDTO> geefAlleLogs() {
        return api.getAlleLogs();
    }

    public LogDTO zoekOpId(int id) {
        return api.zoekOpId(id);
    }

    public void voegLogToe(LogDTO logDTO) {
        api.voegLogToe(logDTO);
    }

}
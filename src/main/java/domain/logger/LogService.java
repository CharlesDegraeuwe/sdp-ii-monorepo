package domain.logger;


import domain.werknemer.Werknemer;

import java.time.LocalDateTime;

public class LogService {

    private final LogRepository repo = new LogRepository();

    public void logActie(Werknemer werknemer, String type, String tabel, Integer recordId, String idk) {
        Log log = new Log();
        log.setWerknemer(werknemer);
        log.setType(type);
        log.setTabel(tabel);
        log.setRecordId(recordId != null ? recordId : 0);
        log.setTimestamp(LocalDateTime.now());
        log.setTest("");
        repo.slaLogOp(log);
    }
}
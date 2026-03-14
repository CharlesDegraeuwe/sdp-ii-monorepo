package domain;

import java.time.LocalDate;

public class LogService {

    private final LogRepository repo = new LogRepository();

    public void logActie(Werknemer werknemer, String type, String tabel, Integer recordId) {
        Log log = new Log();
        log.setWerknemer(werknemer);
        log.setType(type);
        log.setTabel(tabel);
        log.setRecordId(recordId != null ? recordId : 0);
        log.setTimestamp(LocalDate.now());
        log.setTest("");
        repo.slaLogOp(log);
    }
}
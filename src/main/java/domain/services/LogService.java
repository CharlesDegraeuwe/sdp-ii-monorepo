package domain.services;

import domain.auth.Sessie;
import domain.dto.LogDTO;
import domain.dto.WerknemerDTO;
import domain.facades.LogFacade;

import java.time.LocalDateTime;

public class LogService {

    private static final LogFacade facade = new LogFacade();

    public static void log(String type, String tabel, String details) {
        try {
            WerknemerDTO werknemer = Sessie.getInstance().getIngelogdeWerknemer();
            LogDTO logDTO = new LogDTO(0, werknemer, type, tabel, LocalDateTime.now(), details);
            facade.voegLogToe(logDTO);
        } catch (Exception e) {
            // logging mag nooit een actie blokkeren
        }
    }
}

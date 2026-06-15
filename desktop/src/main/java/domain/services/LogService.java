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

            if (werknemer == null) {
                System.out.println("Logging mislukt: geen ingelogde gebruiker");
                return;
            }

            LogDTO logDTO = new LogDTO(
                    null,
                    werknemer.id(),
                    type,
                    tabel,
                    LocalDateTime.now(),
                    details
            );

            facade.voegLogToe(logDTO);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogServiceTest {

    private LogService logService;
    private WerknemerService werknemerService;

    @BeforeEach
    void setUp() {
        logService = new LogService();
        werknemerService = new WerknemerService();
    }

    @Test
    void logActie_metGeldigeWerknemer_gooitGeenException() {
        Werknemer admin = werknemerService.zoekOpEmailEnWachtwoord("admin@email.com", "admin123");
        assertNotNull(admin, "Admin moet bestaan voor deze test");

        assertDoesNotThrow(() ->
                logService.logActie(admin, "RAADPLEGEN", "werknemers", 0)
        );
    }

    @Test
    void logActie_metManagerEnTeam_gooitGeenException() {
        Werknemer manager = werknemerService.zoekOpEmailEnWachtwoord("manager@email.com", "manager123");
        assertNotNull(manager, "Manager moet bestaan voor deze test");

        assertDoesNotThrow(() ->
                logService.logActie(manager, "RAADPLEGEN", "teamwerknemer", 0)
        );
    }
}
package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WerknemerServiceTest {

    private WerknemerService service;

    @BeforeEach
    void setUp() {
        service = new WerknemerService();
    }

    @Test
    void geefAlleWerknemers_geeftLijstTerug() {
        List<Werknemer> werknemers = service.geefAlleWerknemers();
        assertNotNull(werknemers);
        assertFalse(werknemers.isEmpty(), "Er moet minstens één werknemer zijn");
    }

    @Test
    void geefAlleWerknemers_bevatAdmin() {
        List<Werknemer> werknemers = service.geefAlleWerknemers();
        boolean heeftAdmin = werknemers.stream()
                .anyMatch(w -> "Admin".equalsIgnoreCase(w.getRol()));
        assertTrue(heeftAdmin, "Er moet minstens één admin zijn");
    }

    @Test
    void zoekOpEmailEnWachtwoord_correcteCredentials_geeftWerknemerTerug() {
        Werknemer werknemer = service.zoekOpEmailEnWachtwoord("admin@email.com", "admin123");
        assertNotNull(werknemer, "Admin moet gevonden worden");
        assertEquals("Admin", werknemer.getRol());
    }

    @Test
    void zoekOpEmailEnWachtwoord_verkeerdWachtwoord_geeftNullTerug() {
        Werknemer werknemer = service.zoekOpEmailEnWachtwoord("admin@email.com", "foutWachtwoord");
        assertNull(werknemer, "Fout wachtwoord moet null teruggeven");
    }

    @Test
    void geefWerknemersVanManager_onbestaandeManager_geeftLeegeLijst() {
        List<Werknemer> werknemers = service.geefWerknemersVanManager(-1);
        assertNotNull(werknemers);
        assertTrue(werknemers.isEmpty(), "Onbestaande manager moet lege lijst teruggeven");
    }
}
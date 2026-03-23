package domain.facades;

import domain.auth.Sessie;
import domain.dto.WerknemerDTO;
import domain.services.AuthApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthFacadeTest {

    @Mock
    private AuthApiService api;

    @InjectMocks
    private AuthFacade facade;

    private WerknemerDTO werknemer;

    @BeforeEach
    void setUp() {
        werknemer = new WerknemerDTO(1, "Jansen", "Jan", "jan@test.be", "0470123456",
                LocalDate.of(1990, 1, 1), "Werknemer", "actief");
    }

    @Test
    void login_metGeldigeGegevens_retourneertWerknemer() {
        when(api.login("jan@test.be", "wachtwoord123")).thenReturn(werknemer);

        WerknemerDTO result = facade.login("jan@test.be", "wachtwoord123");

        assertEquals(werknemer, result);
        verify(api).login("jan@test.be", "wachtwoord123");
    }

    @Test
    void login_slaatWerknemerOpInSessie() {
        when(api.login("jan@test.be", "wachtwoord123")).thenReturn(werknemer);

        facade.login("jan@test.be", "wachtwoord123");

        assertEquals(werknemer, Sessie.getInstance().getIngelogdeWerknemer());
    }

    @Test
    void login_metLegeEmail_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> facade.login("", "wachtwoord123"));
        verifyNoInteractions(api);
    }

    @Test
    void login_metNullEmail_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> facade.login(null, "wachtwoord123"));
        verifyNoInteractions(api);
    }

    @Test
    void login_metLeegWachtwoord_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> facade.login("jan@test.be", ""));
        verifyNoInteractions(api);
    }

    @Test
    void login_metNullWachtwoord_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> facade.login("jan@test.be", null));
        verifyNoInteractions(api);
    }

    @Test
    void activeerAccount_metGeldigeCode_retourneertTrue() {
        when(api.activeerAccount(1, "ABC123")).thenReturn(true);

        boolean result = facade.activeerAccount(1, "ABC123");

        assertTrue(result);
        verify(api).activeerAccount(1, "ABC123");
    }

    @Test
    void activeerAccount_metLegeCode_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> facade.activeerAccount(1, ""));
        verifyNoInteractions(api);
    }

    @Test
    void activeerAccount_metNullCode_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> facade.activeerAccount(1, null));
        verifyNoInteractions(api);
    }
}

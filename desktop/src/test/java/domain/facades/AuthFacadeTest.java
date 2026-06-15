package domain.facades;

import domain.auth.Sessie;
import domain.dto.LoginResponseDTO;
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
    private LoginResponseDTO loginResponse;

    @BeforeEach
    void setUp() {
        werknemer = new WerknemerDTO(1, "Jansen", "Jan", "jan@test.be", "0470123456",
                LocalDate.of(1990, 1, 1), "Werknemer", "Actief");
        loginResponse = new LoginResponseDTO("jwt-token-123", werknemer);
    }

    @Test
    void verzendLoginEmail_metGeldigEmail_roeptApiAan() {
        doNothing().when(api).verzendLoginEmail("jan@test.be");

        facade.verzendLoginEmail("jan@test.be");

        verify(api).verzendLoginEmail("jan@test.be");
    }

    @Test
    void verzendLoginEmail_metLegeEmail_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> facade.verzendLoginEmail(""));
        verifyNoInteractions(api);
    }

    @Test
    void verzendLoginEmail_metNullEmail_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> facade.verzendLoginEmail(null));
        verifyNoInteractions(api);
    }

    @Test
    void loginMetCode_metGeldigeCode_retourneertWerknemer() {
        when(api.loginMetCode("jan@test.be", "123456")).thenReturn(loginResponse);

        WerknemerDTO result = facade.loginMetCode("jan@test.be", "123456");

        assertEquals(werknemer, result);
        verify(api).loginMetCode("jan@test.be", "123456");
    }

    @Test
    void loginMetCode_slaatWerknemerOpInSessie() {
        when(api.loginMetCode("jan@test.be", "123456")).thenReturn(loginResponse);

        facade.loginMetCode("jan@test.be", "123456");

        assertEquals(werknemer, Sessie.getInstance().getIngelogdeWerknemer());
    }

    @Test
    void loginMetCode_metLegeCode_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> facade.loginMetCode("jan@test.be", ""));
        verifyNoInteractions(api);
    }

    @Test
    void loginMetCode_metNullCode_gooit_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> facade.loginMetCode("jan@test.be", null));
        verifyNoInteractions(api);
    }
}

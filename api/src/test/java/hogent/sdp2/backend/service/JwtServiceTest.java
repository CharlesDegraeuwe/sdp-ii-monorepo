package hogent.sdp2.backend.service;

import static org.assertj.core.api.Assertions.*;

import hogent.sdp2.backend.auth.JwtService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // minimum 32 chars voor HMAC-SHA256
        ReflectionTestUtils.setField(
                jwtService, "secret", "testSecretKeyDieMinimaal32CharsLangMoetZijn!!");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L); // 24u

        userDetails =
                User.withUsername("jan@test.be")
                        .password("password")
                        .authorities(List.of())
                        .build();
    }

    @Test
    void generateToken_genereertNietLeegToken() {
        String token = jwtService.generateToken(userDetails, 1);

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature
    }

    @Test
    void extractUsername_extraheertCorrectEmail() {
        String token = jwtService.generateToken(userDetails, 1);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("jan@test.be");
    }

    @Test
    void isTokenValid_metUserDetails_geeftTrueVoorGeldigToken() {
        String token = jwtService.generateToken(userDetails, 1);

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_metUserDetails_geeftFalseVoorVerkeerdeGebruiker() {
        String token = jwtService.generateToken(userDetails, 1);
        UserDetails andere =
                User.withUsername("andere@test.be").password("pass").authorities(List.of()).build();

        assertThat(jwtService.isTokenValid(token, andere)).isFalse();
    }

    @Test
    void isTokenValid_zonderUserDetails_geeftTrueVoorGeldigToken() {
        String token = jwtService.generateToken(userDetails, 1);

        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_zonderUserDetails_geeftFalseVoorOngeldigToken() {
        assertThat(jwtService.isTokenValid("dit.is.geen.geldig.token")).isFalse();
    }

    @Test
    void isTokenValid_zonderUserDetails_geeftFalseVoorLeegToken() {
        assertThat(jwtService.isTokenValid("")).isFalse();
    }

    @Test
    void isTokenExpired_geeftFalseVoorNieuwToken() {
        String token = jwtService.generateToken(userDetails, 1);

        // Token is geldig, dus isTokenValid moet true zijn
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenExpired_geeftFalseVoorVerlopenToken() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L); // al verlopen
        String token = jwtService.generateToken(userDetails, 1);

        assertThat(jwtService.isTokenValid(token)).isFalse();
    }

    @Test
    void generateToken_tweeTokensVoorVerschillendeUsersZijnVerschillend() {
        UserDetails andere =
                User.withUsername("andere@test.be").password("pass").authorities(List.of()).build();

        String token1 = jwtService.generateToken(userDetails, 1);
        String token2 = jwtService.generateToken(andere, 2);

        assertThat(token1).isNotEqualTo(token2);
    }
}

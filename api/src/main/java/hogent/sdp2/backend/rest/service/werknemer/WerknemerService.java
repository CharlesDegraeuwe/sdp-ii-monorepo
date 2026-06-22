package hogent.sdp2.backend.rest.service.werknemer;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import hogent.sdp2.backend.auth.JwtService;
import hogent.sdp2.backend.domain.Log;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.rest.dto.request.*;
import hogent.sdp2.backend.rest.dto.response.WerknemerResponseDTO;
import hogent.sdp2.backend.rest.repository.LogRepository;
import hogent.sdp2.backend.rest.repository.WerknemerRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class WerknemerService {

    private final WerknemerRepository werknemerRepository;
    private final LogRepository logRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${resend.api-key}")
    private String resendApiKey;

    @Value("${resend.from:delaware suite <no-reply@delaware-suite.com>}")
    private String emailFrom;

    // ==================== AUTH ====================

    public void emailLogin(EmailLoginDTO dto) {
        var werknemer = findByEmailOrThrow(dto.email());

        String code = String.format("%06d", new Random().nextInt(999999));
        werknemer.setActivatieCode(code);
        werknemerRepository.save(werknemer);

        sendEmail(dto.email(), "Login code voor delware suite", buildEmailHtml(code));
    }

    public LoginResponseDTO tokenLogin(TokenLoginDto dto) {
        var werknemer = findByEmailOrThrow(dto.email());

        if (!dto.token().equals(werknemer.getActivatieCode()))
            throw new RuntimeException("Ongeldige code");

        if ("Inactief".equalsIgnoreCase(werknemer.getStatus())) {
            werknemer.setStatus("Actief");
            logActie(
                    "UPDATE",
                    "Account automatisch geactiveerd via token-login voor: " + werknemer.getEmail(),
                    werknemer.getId());
        }

        werknemer.setActivatieCode(null);
        werknemerRepository.save(werknemer);

        String token = generateToken(werknemer);
        return new LoginResponseDTO(token, toDTO(werknemer));
    }

    public LoginResponseDTO passwordLogin(LoginRequestDTO dto) {
        var werknemer = findByEmailOrThrow(dto.email());

        if (!passwordEncoder.matches(dto.wachtwoord(), werknemer.getWachtwoord()))
            throw new RuntimeException("Ongeldige inloggegevens");

        String token = generateToken(werknemer);
        return new LoginResponseDTO(token, toDTO(werknemer));
    }

    public String wijzigWachtwoord(WachtwoordWijzigenDTO dto) {
        var werknemer = findByEmailOrThrow(dto.email());

        if (!passwordEncoder.matches(dto.oudWachtwoord(), werknemer.getWachtwoord()))
            throw new RuntimeException("Het huidige wachtwoord is onjuist");

        if (dto.oudWachtwoord().equals(dto.nieuwWachtwoord()))
            throw new RuntimeException("Nieuw wachtwoord mag niet hetzelfde zijn");

        werknemer.setWachtwoord(passwordEncoder.encode(dto.nieuwWachtwoord()));
        werknemerRepository.save(werknemer);

        logActie(
                "UPDATE",
                "Wachtwoord handmatig gewijzigd door gebruiker: " + werknemer.getEmail(),
                werknemer.getId());

        return "Wachtwoord succesvol gewijzigd";
    }

    public void wachtwoordVergetenAanvragen(WachtwoordVergetenDTO dto) {
        var werknemer = findByEmailOrThrow(dto.email());

        String resetToken = UUID.randomUUID().toString();
        werknemer.setActivatieCode(resetToken);
        werknemerRepository.save(werknemer);

        sendEmail(dto.email(), "Wachtwoord resetten", "<h1>Je reset code: " + resetToken + "</h1>");
    }

    public String resetWachtwoord(WachtwoordResetDTO dto) {
        var werknemer =
                werknemerRepository
                        .findByActivatieCode(dto.resetCode())
                        .orElseThrow(() -> new RuntimeException("Ongeldige reset-code"));

        werknemer.setWachtwoord(passwordEncoder.encode(dto.nieuwWachtwoord()));
        werknemer.setActivatieCode(null);
        werknemerRepository.save(werknemer);

        logActie(
                "UPDATE",
                "Wachtwoord succesvol gereset via e-mail herstelcode voor: " + werknemer.getEmail(),
                werknemer.getId());

        return "Wachtwoord succesvol gereset";
    }

    // ==================== CRUD & LOGICA ====================

    public String maakWerknemer(WerknemerAanmakenDTO dto) {
        if (werknemerRepository.existsByEmail(dto.email()))
            throw new RuntimeException("Er bestaat al een werknemer met email " + dto.email());

        Werknemer werknemer = new Werknemer();
        werknemer.setNaam(dto.naam());
        werknemer.setVoornaam(dto.voornaam());
        werknemer.setEmail(dto.email());
        werknemer.setWachtwoord(passwordEncoder.encode(dto.wachtwoord()));
        werknemer.setTelefoonnummer(dto.telefoonnummer());
        werknemer.setGeboortedatum(dto.geboortedatum());
        werknemer.setRol(dto.rol());
        werknemer.setStatus("Inactief");

        werknemerRepository.save(werknemer);

        logActie(
                "CREATE",
                "Nieuwe medewerker aangemaakt: "
                        + werknemer.getVoornaam()
                        + " "
                        + werknemer.getNaam()
                        + " ("
                        + werknemer.getEmail()
                        + ")",
                werknemer.getId());

        return "Werknemer " + dto.voornaam() + " " + dto.naam() + " succesvol aangemaakt";
    }

    public WerknemerResponseDTO updateUser(UpdateUserDTO dto) {
        var werknemer = findByEmailOrThrow(dto.email());

        // We houden oude waarden bij om een duidelijke logbeschrijving te kunnen maken
        String oudDetails =
                String.format(
                        "Naam: %s %s, Status: %s, GSM: %s",
                        werknemer.getVoornaam(),
                        werknemer.getNaam(),
                        werknemer.getStatus(),
                        werknemer.getTelefoonnummer());

        werknemer.setNaam(dto.naam());
        werknemer.setVoornaam(dto.voornaam());
        werknemer.setStatus(dto.status());
        werknemer.setTelefoonnummer(dto.telefoonnummer());
        werknemer.setGeboortedatum(dto.geboortedatum());

        werknemerRepository.save(werknemer);

        String nieuwDetails =
                String.format(
                        "Naam: %s %s, Status: %s, GSM: %s",
                        dto.voornaam(), dto.naam(), dto.status(), dto.telefoonnummer());

        logActie(
                "UPDATE",
                "Profielgegevens gewijzigd voor "
                        + werknemer.getEmail()
                        + " | Oud: ["
                        + oudDetails
                        + "] -> Nieuw: ["
                        + nieuwDetails
                        + "]",
                werknemer.getId());

        return toDTO(werknemer);
    }

    public List<WerknemerResponseDTO> getAlleUsers() {
        return werknemerRepository.findAll().stream().map(this::toDTO).toList();
    }

    public WerknemerResponseDTO getByEmail(String email) {
        return toDTO(findByEmailOrThrow(email));
    }

    public WerknemerResponseDTO getByID(Integer id) {
        return werknemerRepository
                .findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Werknemer niet gevonden"));
    }

    public String activeerAccount(String activatieCode) {
        var werknemer =
                werknemerRepository
                        .findByActivatieCode(activatieCode)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Ongeldige of al gebruikte activatiecode"));

        werknemer.setStatus("Actief");
        werknemer.setActivatieCode(null);
        werknemerRepository.save(werknemer);

        logActie(
                "UPDATE",
                "Account succesvol geactiveerd via activatiecode: " + werknemer.getEmail(),
                werknemer.getId());

        return "Account succesvol geactiveerd";
    }

    // ==================== ADMIN DIRECTE MUTATIES ====================

    public String updateRolAdmin(Integer id, String nieuweRol) {
        Werknemer werknemer =
                werknemerRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Werknemer met ID " + id + " niet gevonden."));

        String oudeRol = werknemer.getRol();
        werknemer.setRol(nieuweRol);
        werknemerRepository.save(werknemer);

        logActie(
                "UPDATE",
                "Rol gewijzigd van "
                        + oudeRol
                        + " naar "
                        + nieuweRol
                        + " voor: "
                        + werknemer.getEmail(),
                werknemer.getId());

        return "Rol succesvol gewijzigd.";
    }

    public String verwijderWerknemerAdmin(Integer id) {
        Werknemer werknemer =
                werknemerRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Werknemer met ID " + id + " niet gevonden."));

        // Belangrijk: Eerst loggen vóór de delete-actie, zodat we de data nog uit het
        // werknemer-object kunnen lezen!
        logActie(
                "DELETE",
                "Medewerker volledig verwijderd uit systeem: "
                        + werknemer.getVoornaam()
                        + " "
                        + werknemer.getNaam()
                        + " ("
                        + werknemer.getEmail()
                        + ")",
                id);

        werknemerRepository.delete(werknemer);
        return "Werknemer succesvol verwijderd.";
    }

    public String blokkeerWerknemerAdmin(Integer id) {
        Werknemer werknemer =
                werknemerRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Werknemer met ID " + id + " niet gevonden."));

        werknemer.setStatus("Geblokkeerd");
        werknemerRepository.save(werknemer);

        logActie(
                "UPDATE",
                "Status gewijzigd naar [Geblokkeerd] voor: " + werknemer.getEmail(),
                werknemer.getId());

        return "Werknemer succesvol geblokkeerd.";
    }

    public String deactiveerWerknemerAdmin(Integer id) {
        Werknemer werknemer =
                werknemerRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Werknemer met ID " + id + " niet gevonden."));

        werknemer.setStatus("Inactief");
        werknemerRepository.save(werknemer);

        logActie(
                "UPDATE",
                "Status gewijzigd naar [Inactief] voor: " + werknemer.getEmail(),
                werknemer.getId());

        return "Werknemer succesvol gedeactiveerd.";
    }

    public String activeerWerknemerAdmin(Integer id) {
        Werknemer werknemer =
                werknemerRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Werknemer met ID " + id + " niet gevonden."));

        werknemer.setStatus("Actief");
        werknemerRepository.save(werknemer);

        logActie(
                "UPDATE",
                "Status gewijzigd naar [Actief] voor: " + werknemer.getEmail(),
                werknemer.getId());

        return "Werknemer succesvol geactiveerd.";
    }

    public long telAlleWerknemers() {
        return werknemerRepository.count();
    }

    // ==================== HELPER FUNCTIES ====================

    private Werknemer findByEmailOrThrow(String email) {
        return werknemerRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Gebruiker niet gevonden"));
    }

    private WerknemerResponseDTO toDTO(Werknemer w) {
        return new WerknemerResponseDTO(
                w.getId(),
                w.getNaam(),
                w.getVoornaam(),
                w.getEmail(),
                w.getTelefoonnummer(),
                w.getGeboortedatum(),
                w.getRol(),
                w.getStatus());
    }

    private String generateToken(Werknemer werknemer) {
        var userDetails =
                org.springframework.security.core.userdetails.User.builder()
                        .username(werknemer.getEmail())
                        .password(werknemer.getWachtwoord())
                        .roles(werknemer.getRol())
                        .build();
        return jwtService.generateToken(userDetails, werknemer.getId());
    }

    private void sendEmail(String to, String subject, String html) {
        try {
            Resend resend = new Resend(resendApiKey);
            CreateEmailOptions params =
                    CreateEmailOptions.builder()
                            .from(emailFrom)
                            .to(to)
                            .subject(subject)
                            .html(html)
                            .build();
            resend.emails().send(params);
        } catch (Exception e) {
            throw new RuntimeException("Email verzenden mislukt: " + e.getMessage());
        }
    }

    private String buildEmailHtml(String code) {
        return """
        <div style="font-family: sans-serif; max-width: 480px; margin: 0 auto; padding: 40px; height: 100vh">
            <img src="https://i.ibb.co/ksS3Wh05/logo-dark.png" alt="logo" style="height: 50px; margin-bottom: 20px;" />
            <hr style="border: 1px solid #eee;" />
            <div style="margin-top: 20px;">
                <h1 style="font-size: 24px; font-weight: bold;">Login code voor Delaware Suite</h1>
                <p style="color: #555;">Dit is je unieke logincode. Deze blijft 10 minuten geldig, deel dit met niemand.</p>
                <span style="display: inline-block; background: #f4f4f5; border-radius: 12px; padding: 12px 20px; font-size: 28px; font-weight: bold; letter-spacing: 4px;">
                    %s
                </span>
            </div>
        </div>
        """
                .formatted(code);
    }

    /**
     * Centrale private methode om logs direct weg te schrijven. Haalt via Spring Security Context
     * automatisch de uitvoerder (admin/manager) op!
     */
    private void logActie(String type, String beschrijving, Integer recordId) {
        try {
            Werknemer actor = null;

            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null
                    && auth.isAuthenticated()
                    && !auth.getPrincipal().equals("anonymousUser")) {
                actor = werknemerRepository.findByEmail(auth.getName()).orElse(null);
            }

            Log log = new Log();
            log.setWerknemer(actor);
            log.setType(type.toUpperCase());
            log.setTabel("WERKNEMERS");
            log.setRecordId(recordId);
            log.setTimestamp(LocalDateTime.now());
            log.setBeschrijving(beschrijving);

            logRepository.save(log);
        } catch (Exception e) {
            System.err.println("Gefaald om audit-log op te slaan: " + e.getMessage());
        }
    }

    public void verwijderWerknemer(int id) {
        // Check eerst of de werknemer wel bestaat
        if (!werknemerRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Werknemer met ID " + id + " niet gevonden.");
        }

        // Verwijder de werknemer
        werknemerRepository.deleteById(id);
    }
}

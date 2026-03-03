package hogent.sdp2.backend.service;

import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.dto.request.*;
import hogent.sdp2.backend.repository.WerknemerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WerknemerService {

    private final WerknemerRepository werknemerRepository;

    public String maakWerknemer(WerknemerAanmakenDTO dto) {
        if (werknemerRepository.existsByEmail(dto.email())) {
            return "Fout: Er bestaat al een werknemer met e-mailadres " + dto.email();
        }
        String uniekeCode = UUID.randomUUID().toString();

        Werknemer nieuweWerknemer = new Werknemer();
        nieuweWerknemer.setNaam(dto.naam());
        nieuweWerknemer.setVoornaam(dto.voornaam());
        nieuweWerknemer.setEmail(dto.email());
        nieuweWerknemer.setWachtwoord(dto.wachtwoord());
        nieuweWerknemer.setTelefoonnummer(dto.telefoonnummer());
        nieuweWerknemer.setGeboortedatum(dto.geboortedatum());
        nieuweWerknemer.setRol(dto.rol());
        nieuweWerknemer.setStatus("Inactief");
        nieuweWerknemer.setActivatieCode(uniekeCode);

        werknemerRepository.save(nieuweWerknemer);

        return "Werknemer " + dto.voornaam() + " " + dto.naam() + " is succesvol aangemaakt met activatiecode " + uniekeCode;
    }

    public String activeerAccount(String activatieCode) {

        Optional<Werknemer> werknemerOpt = werknemerRepository.findByActivatieCode(activatieCode);

        if (werknemerOpt.isEmpty()) {
            return "Fout: Ongeldige of al gebruikte activatiecode.";
        }

        Werknemer werknemer = werknemerOpt.get();
        werknemer.setStatus("Actief");
        werknemer.setActivatieCode(null);

        werknemerRepository.save(werknemer);
        return "Account succesvol geactiveerd! Je kunt nu inloggen.";
    }

    public WerknemerResponseDTO login(LoginRequestDTO dto) {
        Optional<Werknemer> werknemerOpt = werknemerRepository.findByEmail(dto.email());

        if (werknemerOpt.isEmpty()) throw new RuntimeException("Ongeldige inloggegevens");

        Werknemer werknemer = werknemerOpt.get();

        if (!werknemer.getWachtwoord().equals(dto.wachtwoord()))
            throw new RuntimeException("Ongeldige inloggegevens");

        if ("Inactief".equalsIgnoreCase(werknemer.getStatus()))
            throw new RuntimeException("Account nog niet geactiveerd");

        return new WerknemerResponseDTO(
                werknemer.getId(),
                werknemer.getNaam(),
                werknemer.getVoornaam(),
                werknemer.getEmail(),
                werknemer.getTelefoonnummer(),
                werknemer.getGeboortedatum(),
                werknemer.getRol(),
                werknemer.getStatus()
        );
    }

    public String wijzigWachtwoord(WachtwoordWijzigenDTO dto) {
        Optional<Werknemer> werknemerOpt = werknemerRepository.findByEmail(dto.email());

        if (werknemerOpt.isEmpty()) {
            return "Fout: Gebruiker niet gevonden.";
        }
        Werknemer werknemer = werknemerOpt.get();

        if (!werknemer.getWachtwoord().equals(dto.oudWachtwoord())) {
            return "Fout: Het huidige wachtwoord is onjuist.";
        }

        if (dto.oudWachtwoord().equals(dto.nieuwWachtwoord())) {
            return "Fout: Het nieuwe wachtwoord mag niet hetzelfde zijn als het huidige wachtwoord.";
        }

        werknemer.setWachtwoord(dto.nieuwWachtwoord());
        werknemerRepository.save(werknemer);
        return "Wachtwoord succesvol gewijzigd!";
    }

    public String wachtwoordVergetenAanvragen(WachtwoordVergetenDTO dto) {
        Optional<Werknemer> werknemerOpt = werknemerRepository.findByEmail(dto.email());
        if (werknemerOpt.isPresent()) {
            Werknemer werknemer = werknemerOpt.get();
            String resetToken = java.util.UUID.randomUUID().toString();
            werknemer.setActivatieCode(resetToken);
            werknemerRepository.save(werknemer);

            return "Je code is: " + resetToken;
        }
        return "Email niet gevonden.";
    }

    public String resetWachtwoord(WachtwoordResetDTO dto) {
        Optional<Werknemer> werknemerOpt = werknemerRepository.findByActivatieCode(dto.resetCode());

        if (werknemerOpt.isEmpty()) {
            return "Fout: De reset-code is ongeldig.";
        }
        Werknemer werknemer = werknemerOpt.get();
        werknemer.setWachtwoord(dto.nieuwWachtwoord());
        werknemer.setActivatieCode(null);
        werknemerRepository.save(werknemer);
        return "Je wachtwoord is succesvol gereset! Je kunt nu inloggen.";
    }

    public List<WerknemerResponseDTO> getAlleUsers() {
        return werknemerRepository.findAll().stream()
                .map(w -> new WerknemerResponseDTO(
                        w.getId(),
                        w.getNaam(),
                        w.getVoornaam(),
                        w.getEmail(),
                        w.getTelefoonnummer(),
                        w.getGeboortedatum(),
                        w.getRol(),
                        w.getStatus()
                ))
                .toList();
    }

    public WerknemerResponseDTO updateUser(UpdateUserDTO dto) {
        Optional<Werknemer> werknemerOpt = werknemerRepository.findByEmail(dto.email());

        if (werknemerOpt.isEmpty()) {
            throw new RuntimeException("Fout: Gebruiker niet gevonden.");
        }
        Werknemer werknemer = werknemerOpt.get();

        werknemer.setNaam(dto.naam());
        werknemer.setVoornaam(dto.voornaam());
        werknemer.setEmail(dto.email());
        werknemer.setStatus(dto.status());
        werknemer.setTelefoonnummer(dto.telefoonnummer());
        werknemer.setGeboortedatum(dto.geboortedatum());

        werknemerRepository.save(werknemer);
        return new WerknemerResponseDTO(
                werknemer.getId(),
                werknemer.getNaam(),
                werknemer.getVoornaam(),
                werknemer.getEmail(),
                werknemer.getStatus(),
                werknemer.getGeboortedatum(),
                werknemer.getTelefoonnummer(),
                werknemer.getRol()
        );
    }

    public WerknemerResponseDTO getByEmail(String email) {
        Optional<Werknemer> werknemerOpt = werknemerRepository.findByEmail(email);
        if (werknemerOpt.isEmpty()) {
            throw new RuntimeException("Fout: Gebruiker niet gevonden.");
        }
        Werknemer werknemer = werknemerOpt.get();

        return new WerknemerResponseDTO(
                werknemer.getId(),
                werknemer.getNaam(),
                werknemer.getVoornaam(),
                werknemer.getEmail(),
                werknemer.getStatus(),
                werknemer.getGeboortedatum(),
                werknemer.getTelefoonnummer(),
                werknemer.getRol()
        );
    }

    public WerknemerResponseDTO getByID(Integer id) {
        return werknemerRepository.findById(id)
                .map(w -> new WerknemerResponseDTO(
                        w.getId(),
                        w.getNaam(),
                        w.getVoornaam(),
                        w.getEmail(),
                        w.getTelefoonnummer(),
                        w.getGeboortedatum(),
                        w.getRol(),
                        w.getStatus()
                ))
                .orElseThrow(() -> new RuntimeException("Werknemer niet gevonden"));
    }
}

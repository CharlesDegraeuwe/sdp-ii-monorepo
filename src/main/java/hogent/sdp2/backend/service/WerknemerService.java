package hogent.sdp2.backend.service;

import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.dto.request.WerknemerAanmakenDTO;
import hogent.sdp2.backend.dto.request.LoginRequestDTO;
import hogent.sdp2.backend.repository.WerknemerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public String login(LoginRequestDTO dto) {
        Optional<Werknemer> werknemerOpt = werknemerRepository.findByEmail(dto.email());

        if (werknemerOpt.isEmpty()) {
            return "Fout: Ongeldige inloggegevens.";
        }

        Werknemer werknemer = werknemerOpt.get();

        if (!werknemer.getWachtwoord().equals(dto.wachtwoord())) {
            return "Fout: Ongeldige inloggegevens.";
        }

        if ("Inactief".equalsIgnoreCase(werknemer.getStatus())) {
            return "Fout: Je account is nog niet geactiveerd. Controleer je activatiecode of e-mail.";
        }
        return "Succesvol ingelogd! Welkom terug, " + werknemer.getVoornaam() + ".";
    }
}

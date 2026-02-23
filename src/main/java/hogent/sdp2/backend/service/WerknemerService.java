package hogent.sdp2.backend.service;

import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.dto.request.WerknemerAanmakenDTO;
import hogent.sdp2.backend.repository.WerknemerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WerknemerService {

    private final WerknemerRepository werknemerRepository;

    public String maakWerknemer(WerknemerAanmakenDTO dto) {
        if (werknemerRepository.existsByEmail(dto.email())) {
            return "Fout: Er bestaat al een werknemer met e-mailadres " + dto.email();
        }

        Werknemer nieuweWerknemer = new Werknemer();
        nieuweWerknemer.setNaam(dto.naam());
        nieuweWerknemer.setVoornaam(dto.voornaam());
        nieuweWerknemer.setEmail(dto.email());
        nieuweWerknemer.setWachtwoord(dto.wachtwoord());
        nieuweWerknemer.setTelefoonnummer(dto.telefoonnummer());
        nieuweWerknemer.setGeboortedatum(dto.geboortedatum());
        nieuweWerknemer.setRol(dto.rol());
        nieuweWerknemer.setStatus(dto.status());

        werknemerRepository.save(nieuweWerknemer);

        return "Werknemer " + dto.voornaam() + " " + dto.naam() + " is succesvol aangemaakt!";
    }
}
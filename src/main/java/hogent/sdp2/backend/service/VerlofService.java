package hogent.sdp2.backend.service;

import hogent.sdp2.backend.domain.Verlof;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.dto.request.VerlofAanvragenDTO;
import hogent.sdp2.backend.repository.TeamwerknemerRepository;
import hogent.sdp2.backend.repository.VerlofRepository;
import hogent.sdp2.backend.repository.WerknemerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerlofService {

    private final VerlofRepository verlofRepository;
    private final WerknemerRepository werknemerRepository;
    private final TeamwerknemerRepository teamwerknemerRepository;

    public String vraagVerlofAan(VerlofAanvragenDTO dto) {
        Werknemer werknemer = werknemerRepository.findById(dto.werknemerId())
                .orElseThrow(() -> new RuntimeException("Werknemer niet gevonden"));

        if (dto.eindDatum().isBefore(dto.startDatum())) {
            throw new RuntimeException("Einddatum mag niet voor startdatum liggen.");
        }

        // Zoek manager of supervisor van het team
        Integer goedkeurderId = teamwerknemerRepository
                .findByWerknemerId(dto.werknemerId())
                .stream()
                .findFirst()
                .flatMap(tw -> teamwerknemerRepository.findGoedkeurderVanTeam(tw.getTeam().getId()))
                .map(tw -> tw.getWerknemer().getId())
                .orElse(null);

        if (goedkeurderId == null) {
            throw new RuntimeException("Geen manager of supervisor gevonden voor deze werknemer.");
        }

        Verlof verlof = new Verlof();
        verlof.setWerknemer(werknemer);
        verlof.setStartDatum(dto.startDatum());
        verlof.setEindDatum(dto.eindDatum());
        verlof.setType(dto.type());
        verlof.setStatus("In afwachting");
        verlof.setGoedkeurderId(goedkeurderId);

        verlofRepository.save(verlof);
        return "Verlofaanvraag succesvol ingediend.";
    }
}
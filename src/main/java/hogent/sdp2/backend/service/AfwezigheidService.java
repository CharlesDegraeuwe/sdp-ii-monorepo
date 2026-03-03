package hogent.sdp2.backend.service;

import hogent.sdp2.backend.domain.Afwezigheid;
import hogent.sdp2.backend.domain.Werknemer;
import hogent.sdp2.backend.dto.request.AfwezigheidAanmakenDTO;
import hogent.sdp2.backend.repository.AfwezigheidRepository;
import hogent.sdp2.backend.repository.WerknemerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AfwezigheidService {

    private final AfwezigheidRepository afwezigheidRepository;
    private final WerknemerRepository werknemerRepository;

    public String meldAfwezigheid(AfwezigheidAanmakenDTO dto) {
        Werknemer werknemer = werknemerRepository.findById(dto.werknemerId())
                .orElseThrow(() -> new RuntimeException("Werknemer niet gevonden"));

        if (dto.eindDatum().isBefore(dto.startDatum())) {
            throw new RuntimeException("Einddatum mag niet voor startdatum liggen.");
        }

        Afwezigheid afwezigheid = new Afwezigheid();
        afwezigheid.setWerknemer(werknemer);
        afwezigheid.setStartDatum(dto.startDatum());
        afwezigheid.setEindDatum(dto.eindDatum());
        afwezigheid.setReden(dto.reden());
        afwezigheid.setCertificaat(dto.certificaat());

        afwezigheidRepository.save(afwezigheid);
        return "Afwezigheid succesvol gemeld.";
    }
}
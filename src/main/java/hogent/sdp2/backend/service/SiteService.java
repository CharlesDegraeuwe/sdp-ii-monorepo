package hogent.sdp2.backend.service;

import hogent.sdp2.backend.domain.Site;
import hogent.sdp2.backend.dto.request.SiteAanmakenDTO;
import hogent.sdp2.backend.dto.request.SiteWijzigenDTO;
import hogent.sdp2.backend.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SiteService {

    private final SiteRepository siteRepository;

    public String maakSite(SiteAanmakenDTO dto) {
        log.info("Audit: Poging tot aanmaken nieuwe site: {}", dto.naam());

        if (siteRepository.existsByNaam(dto.naam())) {
            log.warn("Audit: Aanmaken site mislukt. Site met naam {} bestaat al.", dto.naam());
            return "Fout: Er bestaat al een site met deze naam.";
        }

        Site nieuweSite = new Site();
        nieuweSite.setNaam(dto.naam());
        nieuweSite.setStad(dto.stad());
        nieuweSite.setLand(dto.land());
        nieuweSite.setLongitude(dto.longitude());
        nieuweSite.setLatitude(dto.latitude());

        siteRepository.save(nieuweSite);

        log.info("Audit: Site {} succesvol opgeslagen in database.", dto.naam());
        return "Site '" + dto.naam() + "' is succesvol aangemaakt in " + dto.stad() + "!";
    }

    public String wijzigSite(Integer id, SiteWijzigenDTO dto) {
        log.info("Audit: Poging tot wijzigen van site met ID: {}", id);

        Optional<Site> siteOpt = siteRepository.findById(id);

        if (siteOpt.isEmpty()) {
            log.warn("Audit: Wijzigen mislukt. Site met ID {} bestaat niet.", id);
            return "Fout: De opgevraagde site is niet gevonden.";
        }

        Site site = siteOpt.get();

        if (!site.getNaam().equals(dto.naam()) && siteRepository.existsByNaam(dto.naam())) {
            log.warn("Audit: Wijzigen mislukt. Nieuwe naam '{}' is al in gebruik.", dto.naam());
            return "Fout: Er bestaat al een andere site met deze naam.";
        }

        site.setNaam(dto.naam());
        site.setStad(dto.stad());
        site.setLand(dto.land());
        site.setLongitude(dto.longitude());
        site.setLatitude(dto.latitude());

        siteRepository.save(site);

        log.info("Audit: Site {} (ID: {}) succesvol gewijzigd.", dto.naam(), id);
        return "Site '" + dto.naam() + "' is succesvol bijgewerkt!";
    }

    public String verwijderSite(Integer id) {
        log.info("Audit: Poging tot verwijderen van site met ID: {}", id);

        if (!siteRepository.existsById(id)) {
            log.warn("Audit: Verwijderen mislukt. Site met ID {} bestaat niet.", id);
            return "Fout: De opgevraagde site is niet gevonden.";
        }

        siteRepository.deleteById(id);

        log.info("Audit: Site met ID {} is succesvol verwijderd.", id);
        return "Site succesvol verwijderd!";
    }
}
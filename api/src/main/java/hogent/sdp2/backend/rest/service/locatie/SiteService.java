package hogent.sdp2.backend.rest.service.locatie;

import hogent.sdp2.backend.domain.Site;
import hogent.sdp2.backend.rest.dto.request.SiteAanmakenDTO;
import hogent.sdp2.backend.rest.dto.request.SiteWijzigenDTO;
import hogent.sdp2.backend.rest.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
        nieuweSite.setLocatie(dto.locatie());
        nieuweSite.setCapaciteit(dto.capaciteit());
        nieuweSite.setStatus(dto.status());

        siteRepository.save(nieuweSite);

        log.info("Audit: Site {} succesvol opgeslagen in database.", dto.naam());
        return "Site '" + dto.naam() + "' is succesvol aangemaakt in " + dto.locatie() + "!";
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
        site.setLocatie(dto.locatie());
        site.setCapaciteit(dto.capaciteit());
        site.setStatus(dto.status());

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

    public List<Site> haalAlleSitesOp() {
        log.info("Audit: Alle sites worden opgevraagd.");
        return siteRepository.findAll();
    }

    public Site haalSiteOpId(Integer id) {
        log.info("Audit: Site met ID {} wordt opgevraagd.", id);
        return siteRepository.findById(id).orElse(null);
    }

    public List<Site> haalSitesVanWerknemer(Integer werknemerId) {
        log.info("Audit: Sites voor werknemer {} worden opgevraagd.", werknemerId);
        return siteRepository.findAll();
    }
}
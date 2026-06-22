package hogent.sdp2.backend.rest.service.locatie;

import hogent.sdp2.backend.domain.*;
import hogent.sdp2.backend.rest.dto.request.SiteAanmakenDTO;
import hogent.sdp2.backend.rest.dto.request.SiteStatsDTO;
import hogent.sdp2.backend.rest.dto.request.SiteWijzigenDTO;
import hogent.sdp2.backend.rest.repository.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SiteService {

    private final SiteRepository siteRepository;
    private final AfwezigheidRepository afwezigheidRepository;
    private final TeamwerknemerRepository teamwerknemerRepository;
    private final SiteteamRepository siteteamRepository;
    private final WerknemerRepository werknemerRepository;

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
        Werknemer ingelogdeGebruiker =
                werknemerRepository
                        .findById(werknemerId)
                        .orElseThrow(() -> new RuntimeException("Gebruiker niet gevonden"));

        if (ingelogdeGebruiker.getRol() != null && ingelogdeGebruiker.getRol().equals("Admin")) {
            return siteRepository.findAll();
        } else {
            return siteRepository.findSitesByWerknemerId(werknemerId);
        }
    }

    public SiteStatsDTO getSiteStatsVoorWerknemer(Integer werknemerId) {

        Werknemer ingelogdeGebruiker =
                werknemerRepository
                        .findById(werknemerId)
                        .orElseThrow(() -> new RuntimeException("Gebruiker niet gevonden"));

        List<Site> sites;

        if (ingelogdeGebruiker.getRol() != null && ingelogdeGebruiker.getRol().equals("Admin")) {
            sites = siteRepository.findAll();
        } else {
            sites = siteRepository.findSitesByWerknemerId(werknemerId);
        }

        Set<Werknemer> uniekeWerknemers = new HashSet<>();

        for (Site site : sites) {
            List<Siteteam> siteKoppelingen = siteteamRepository.findBySite(site);

            for (Siteteam st : siteKoppelingen) {
                Team team = st.getTeam();

                if (team != null) {
                    List<Teamwerknemer> werknemerKoppelingen =
                            teamwerknemerRepository.findByTeam(team);

                    for (Teamwerknemer tw : werknemerKoppelingen) {
                        if (tw.getWerknemer() != null) {
                            uniekeWerknemers.add(tw.getWerknemer());
                        }
                    }
                }
            }
        }

        int afwezigen = 0;
        LocalDate vandaag = LocalDate.now();

        for (Werknemer w : uniekeWerknemers) {
            boolean isAfwezig = afwezigheidRepository.isWerknemerAfwezigOpDatum(w.getId(), vandaag);
            if (isAfwezig) {
                afwezigen++;
            }
        }
        return new SiteStatsDTO(uniekeWerknemers.size(), afwezigen);
    }

    public int getActieveSitesPercentage() {
        long totaalSites = siteRepository.count();

        if (totaalSites == 0) {
            return 0;
        }

        long actieveSites = siteRepository.countByStatus("Actief");

        return (int) Math.round((double) actieveSites / totaalSites * 100);
    }
}

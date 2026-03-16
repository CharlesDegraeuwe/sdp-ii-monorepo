package hogent.sdp2.backend.controller;

import hogent.sdp2.backend.domain.Site;
import hogent.sdp2.backend.dto.request.SiteAanmakenDTO;
import hogent.sdp2.backend.dto.request.SiteWijzigenDTO;
import hogent.sdp2.backend.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
public class SiteController {

    private final SiteService siteService;

    @PostMapping
    public String createSite(@RequestBody SiteAanmakenDTO dto) {
        return siteService.maakSite(dto);
    }

    @PutMapping("/{id}")
    public String updateSite(@PathVariable Integer id, @RequestBody SiteWijzigenDTO dto) {
        return siteService.wijzigSite(id, dto);
    }

    @DeleteMapping("/{id}")
    public String deleteSite(@PathVariable Integer id) {
        return siteService.verwijderSite(id);
    }

    @GetMapping
    public ResponseEntity<List<Site>> getAlleSites() {
        List<Site> sites = siteService.haalAlleSitesOp();
        return ResponseEntity.ok(sites); // Geeft een 200 OK met de lijst in JSON terug
    }

    @GetMapping("/{id}")
    public ResponseEntity<Site> getSiteById(@PathVariable Integer id) {
        Site site = siteService.haalSiteOpId(id);
        if (site == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(site);
    }
}
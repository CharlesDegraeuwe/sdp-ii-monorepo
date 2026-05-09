package hogent.sdp2.backend.REST.controller;

import hogent.sdp2.backend.domain.Site;
import hogent.sdp2.backend.REST.dto.request.MachineWijzigenDTO;
import hogent.sdp2.backend.REST.dto.request.SiteAanmakenDTO;
import hogent.sdp2.backend.REST.dto.request.SiteWijzigenDTO;
import hogent.sdp2.backend.REST.service.locatie.MachineService;
import hogent.sdp2.backend.REST.service.locatie.SiteService;
import hogent.sdp2.backend.auth.SessieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
public class SiteController {

    private final SiteService siteService;
    private final MachineService machineService;
    private final SessieService sessieService;

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @PostMapping
    public String createSite(@RequestBody SiteAanmakenDTO dto) {
        return siteService.maakSite(dto);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @PutMapping("/{id}")
    public String updateSite(@PathVariable Integer id, @RequestBody SiteWijzigenDTO dto) {
        return siteService.wijzigSite(id, dto);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager')")
    @DeleteMapping("/{id}")
    public String deleteSite(@PathVariable Integer id) {
        return siteService.verwijderSite(id);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    @GetMapping
    public ResponseEntity<List<Site>> getAlleSites() {
        List<Site> sites = siteService.haalAlleSitesOp();
        return ResponseEntity.ok(sites);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    @GetMapping("/{id}")
    public ResponseEntity<Site> getSiteById(@PathVariable Integer id) {
        Site site = siteService.haalSiteOpId(id);
        if (site == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(site);
    }

    @PreAuthorize("hasAnyRole('Admin', 'Manager', 'Supervisor')")
    @GetMapping("/{id}/machines")
    public ResponseEntity<List<MachineWijzigenDTO>> getMachinesVanSite(@PathVariable Integer id) {
        List<MachineWijzigenDTO> machines = machineService.haalMachinesOpVoorSite(id);
        return ResponseEntity.ok(machines);
    }

    @GetMapping("/werknemer/{werknemerId}")
    public ResponseEntity<List<Site>> getSitesVanWerknemer(@PathVariable Integer werknemerId) {
        sessieService.assertToegangTotWerknemer(werknemerId);
        List<Site> sites = siteService.haalSitesVanWerknemer(werknemerId);
        return ResponseEntity.ok(sites);
    }
}

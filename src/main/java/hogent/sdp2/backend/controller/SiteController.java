package hogent.sdp2.backend.controller;

import hogent.sdp2.backend.dto.request.SiteAanmakenDTO;
import hogent.sdp2.backend.dto.request.SiteWijzigenDTO;
import hogent.sdp2.backend.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
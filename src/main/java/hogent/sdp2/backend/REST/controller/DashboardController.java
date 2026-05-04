package hogent.sdp2.backend.REST.controller;

import hogent.sdp2.backend.REST.dto.response.DashboardResponseDTO;
import hogent.sdp2.backend.REST.service.overzicht.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardResponseDTO getOverzicht() {
        return dashboardService.getOverzicht();
    }
}
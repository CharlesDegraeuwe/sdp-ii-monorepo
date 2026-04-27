package hogent.sdp2.backend.controller;

import hogent.sdp2.backend.dto.response.DashboardResponseDTO;
import hogent.sdp2.backend.service.DashboardService;
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
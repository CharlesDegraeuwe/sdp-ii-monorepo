package hogent.sdp2.backend.rest.controller;

import hogent.sdp2.backend.rest.dto.request.LogDTO;
import hogent.sdp2.backend.rest.service.logger.LogService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('Admin', 'Manager')")
public class LogController {

    private final LogService logService;

    @PostMapping
    public String createLog(@RequestBody LogDTO dto) {
        return logService.maakLog(dto);
    }

    @GetMapping("/logs")
    public List<LogDTO> getAlleLogs() {
        return logService.getAlleLogs();
    }

    @GetMapping("/byid")
    public LogDTO getById(@RequestParam Integer id) {
        return logService.getByID(id);
    }

    @GetMapping("/recent")
    public List<LogDTO> getRecenteLogs() {
        return logService.getRecenteLogs();
    }
}

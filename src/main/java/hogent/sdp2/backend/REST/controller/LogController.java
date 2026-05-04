package hogent.sdp2.backend.REST.controller;

import hogent.sdp2.backend.REST.dto.request.LogDTO;
import hogent.sdp2.backend.REST.service.logger.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
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

}


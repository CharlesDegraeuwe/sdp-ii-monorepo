package hogent.sdp2.backend.controller;

import hogent.sdp2.backend.dto.request.*;
import hogent.sdp2.backend.service.LogService;
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


package hogent.sdp2.backend.controller;

import hogent.sdp2.backend.dto.request.LogRequestDTO;
import hogent.sdp2.backend.dto.response.LogResponseDTO;
import hogent.sdp2.backend.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LogResponseDTO slaLogOp(@RequestBody LogRequestDTO dto) {
        return logService.slaLogOp(dto);
    }

    @GetMapping
    public List<LogResponseDTO> geefAlleLogs() {
        return logService.geefAlleLogs();
    }

    @GetMapping("/werknemer/{werknemerId}")
    public List<LogResponseDTO> geefLogsVanWerknemer(@PathVariable Integer werknemerId) {
        return logService.geefLogsVanWerknemer(werknemerId);
    }
}
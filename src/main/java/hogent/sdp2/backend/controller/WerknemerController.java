package hogent.sdp2.backend.controller;

import hogent.sdp2.backend.dto.request.WerknemerAanmakenDTO;
import hogent.sdp2.backend.service.WerknemerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/werknemers")
@RequiredArgsConstructor
public class WerknemerController {

    private final WerknemerService werknemerService;

    @PostMapping
    public String createWerknemer(@RequestBody WerknemerAanmakenDTO dto) {
        return werknemerService.maakWerknemer(dto);
    }

    @PostMapping("/activeer")
    public String activeerWerknemer(@RequestParam String code) {
        return werknemerService.activeerAccount(code);
    }
}


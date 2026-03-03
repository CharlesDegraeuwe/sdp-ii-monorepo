package hogent.sdp2.backend.controller;

import hogent.sdp2.backend.dto.request.AfwezigheidAanmakenDTO;
import hogent.sdp2.backend.service.AfwezigheidService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/afwezigheid")
@RequiredArgsConstructor
public class AfwezigheidController {

    private final AfwezigheidService afwezigheidService;

    @PostMapping
    public String meldAfwezigheid(@RequestBody AfwezigheidAanmakenDTO dto) {
        return afwezigheidService.meldAfwezigheid(dto);
    }
}
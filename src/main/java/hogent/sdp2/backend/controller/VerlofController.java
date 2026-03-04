package hogent.sdp2.backend.controller;

import hogent.sdp2.backend.dto.request.VerlofAanvragenDTO;
import hogent.sdp2.backend.service.VerlofService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verlof")
@RequiredArgsConstructor
public class VerlofController {

    private final VerlofService verlofService;

    @PostMapping
    public String vraagVerlofAan(@RequestBody VerlofAanvragenDTO dto) {
        return verlofService.vraagVerlofAan(dto);
    }
}
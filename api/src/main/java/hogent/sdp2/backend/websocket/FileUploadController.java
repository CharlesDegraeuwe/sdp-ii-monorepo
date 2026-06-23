package hogent.sdp2.backend.websocket;

import hogent.sdp2.backend.websocket.dto.FileAttachmentDTO;
import hogent.sdp2.backend.auth.SessieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;
    private final SessieService sessieService;

    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<FileAttachmentDTO>> uploadFiles(
            @RequestParam("files") List<MultipartFile> files
    ) {
        int userId = sessieService.getIngelogdeWerknemerId();

        List<FileAttachmentDTO> results = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                FileStorageService.StoredFile stored = fileStorageService.store(userId, file);
                results.add(new FileAttachmentDTO(
                        stored.id(),
                        stored.originalName(),
                        stored.mimeType(),
                        file.getSize()
                ));
            } catch (IOException e) {
                return ResponseEntity.internalServerError().build();
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok(results);
    }
}

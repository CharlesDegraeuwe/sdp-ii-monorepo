package hogent.sdp2.backend.websocket;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/png", "image/jpeg", "image/gif",
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    public record StoredFile(String id, String originalName, String mimeType, byte[] data) {
        public Resource asResource() {
            return new ByteArrayResource(data);
        }

        public MimeType mimeTypeObj() {
            return MimeType.valueOf(mimeType);
        }
    }

    // userId -> (fileId -> StoredFile)
    private final Map<Integer, Map<String, StoredFile>> store = new ConcurrentHashMap<>();

    public StoredFile store(int userId, MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Niet-ondersteund bestandstype: " + contentType +
                    ". Toegestaan: afbeeldingen (png/jpg/gif), PDF, Excel (.xlsx), Word (.docx)");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Bestand te groot. Maximum is 10 MB.");
        }

        String id = UUID.randomUUID().toString();
        StoredFile stored = new StoredFile(id, file.getOriginalFilename(), contentType, file.getBytes());
        store.computeIfAbsent(userId, k -> new ConcurrentHashMap<>()).put(id, stored);
        return stored;
    }

    public List<StoredFile> resolve(int userId, List<String> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) return List.of();
        Map<String, StoredFile> userFiles = store.getOrDefault(userId, Map.of());
        return fileIds.stream()
                .map(userFiles::get)
                .filter(Objects::nonNull)
                .toList();
    }

    public void cleanup(int userId, List<String> fileIds) {
        Map<String, StoredFile> userFiles = store.get(userId);
        if (userFiles != null && fileIds != null) {
            fileIds.forEach(userFiles::remove);
        }
    }
}

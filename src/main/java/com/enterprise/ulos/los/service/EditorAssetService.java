package com.enterprise.ulos.los.service;

import com.enterprise.ulos.los.entity.MasterPortfolioLookupEntity;
import com.enterprise.ulos.los.model.EditorAssetApiModels;
import com.enterprise.ulos.los.repository.MasterPortfolioLookupRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
public class EditorAssetService {

    private static final String POLICY_MASTER_TYPE = "editor_upload_policy";
    private static final String POLICY_MAX_SIZE_MB_CODE = "MAX_SIZE_MB";
    private static final int DEFAULT_MAX_SIZE_MB = 5;
    private static final Set<String> DEFAULT_ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "webp", "gif");
    private static final Pattern SAFE_FILE_NAME = Pattern.compile("^[A-Za-z0-9._-]+$");

    private final MasterPortfolioLookupRepository masterPortfolioLookupRepository;
    private final Path uploadRootPath;

    public EditorAssetService(
            MasterPortfolioLookupRepository masterPortfolioLookupRepository,
            @Value("${ulos.editor.image-upload-dir:data/editor-images}") String uploadDirectory
    ) {
        this.masterPortfolioLookupRepository = masterPortfolioLookupRepository;
        this.uploadRootPath = Paths.get(uploadDirectory).toAbsolutePath().normalize();
    }

    @Transactional
    public EditorAssetApiModels.EditorImageUploadResponse uploadEditorImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file is required");
        }

        UploadPolicy policy = resolvePolicy();
        String contentType = safeLower(file.getContentType());
        if (!contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only image files are allowed");
        }

        String originalFileName = file.getOriginalFilename();
        String extension = normalizeExtension(extractExtension(originalFileName));
        if (extension == null) {
            extension = extensionFromContentType(contentType);
        }
        if (extension == null || !policy.allowedExtensions().contains(extension)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid file extension. Allowed: " + String.join(", ", policy.allowedExtensions())
            );
        }

        long maxBytes = (long) policy.maxSizeMb() * 1024L * 1024L;
        if (file.getSize() > maxBytes) {
            throw new ResponseStatusException(
                    HttpStatus.PAYLOAD_TOO_LARGE,
                    "File size exceeds max limit of " + policy.maxSizeMb() + " MB"
            );
        }

        String storedFileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        Path targetPath = uploadRootPath.resolve(storedFileName).normalize();
        if (!targetPath.startsWith(uploadRootPath)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid upload path");
        }

        try {
            Files.createDirectories(uploadRootPath);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store image file");
        }

        return new EditorAssetApiModels.EditorImageUploadResponse(
                "/api/public/editor-images/" + storedFileName,
                storedFileName,
                file.getSize(),
                extension,
                policy.maxSizeMb(),
                policy.allowedExtensions().stream().toList()
        );
    }

    public Resource loadEditorImage(String fileName) {
        if (fileName == null || fileName.isBlank() || !SAFE_FILE_NAME.matcher(fileName).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file name");
        }
        Path filePath = uploadRootPath.resolve(fileName).normalize();
        if (!filePath.startsWith(uploadRootPath)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file path");
        }
        if (!Files.exists(filePath)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image file not found");
        }
        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to resolve image file");
        }
    }

    private UploadPolicy resolvePolicy() {
        List<MasterPortfolioLookupEntity> rows = masterPortfolioLookupRepository
                .findAllByMasterTypeIgnoreCaseAndActiveTrueOrderBySortOrderAscItemNameAsc(POLICY_MASTER_TYPE);

        LinkedHashSet<String> allowedExtensions = new LinkedHashSet<>();
        int maxSizeMb = DEFAULT_MAX_SIZE_MB;

        for (MasterPortfolioLookupEntity row : rows) {
            String code = safeUpper(row.getItemCode());
            String name = row.getItemName() == null ? "" : row.getItemName().trim();

            if (POLICY_MAX_SIZE_MB_CODE.equals(code)) {
                Integer parsed = parsePositiveInteger(name);
                if (parsed != null) {
                    maxSizeMb = parsed;
                }
                continue;
            }

            String extension = resolveExtensionFromPolicy(code, name);
            if (extension != null) {
                allowedExtensions.add(extension);
            }
        }

        if (allowedExtensions.isEmpty()) {
            allowedExtensions.addAll(DEFAULT_ALLOWED_EXTENSIONS);
        }

        return new UploadPolicy(allowedExtensions, maxSizeMb);
    }

    private String resolveExtensionFromPolicy(String code, String name) {
        String candidate = null;
        if (code.startsWith("ALLOW_EXT_")) {
            candidate = code.substring("ALLOW_EXT_".length());
        } else if (code.startsWith("ALLOWED_EXT_")) {
            candidate = code.substring("ALLOWED_EXT_".length());
        } else if (code.startsWith("EXT_")) {
            candidate = code.substring("EXT_".length());
        }

        if (candidate == null || candidate.isBlank()) {
            candidate = name;
        }

        String normalized = normalizeExtension(candidate);
        if (normalized == null) {
            return null;
        }
        if (!isKnownImageExtension(normalized)) {
            return null;
        }
        return normalized;
    }

    private boolean isKnownImageExtension(String extension) {
        return Set.of("png", "jpg", "jpeg", "webp", "gif", "bmp", "svg").contains(extension);
    }

    private String extensionFromContentType(String contentType) {
        if (contentType == null) {
            return null;
        }
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/png" -> "png";
            case "image/jpg", "image/jpeg" -> "jpg";
            case "image/webp" -> "webp";
            case "image/gif" -> "gif";
            case "image/bmp" -> "bmp";
            case "image/svg+xml" -> "svg";
            default -> null;
        };
    }

    private String extractExtension(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return null;
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot < 0 || lastDot == fileName.length() - 1) {
            return null;
        }
        return fileName.substring(lastDot + 1);
    }

    private String normalizeExtension(String raw) {
        if (raw == null) {
            return null;
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if (normalized.startsWith(".")) {
            normalized = normalized.substring(1);
        }
        if (!normalized.matches("[a-z0-9]+")) {
            return null;
        }
        return normalized;
    }

    private Integer parsePositiveInteger(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            int value = Integer.parseInt(raw.trim());
            return value > 0 ? value : null;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private String safeLower(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.ROOT);
    }

    private String safeUpper(String value) {
        if (value == null) {
            return "";
        }
        return value.toUpperCase(Locale.ROOT);
    }

    private record UploadPolicy(Set<String> allowedExtensions, int maxSizeMb) {
    }
}

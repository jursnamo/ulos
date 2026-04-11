package com.enterprise.ulos.los.controller;

import com.enterprise.ulos.los.model.EditorAssetApiModels;
import com.enterprise.ulos.los.service.EditorAssetService;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class EditorAssetController {

    private final EditorAssetService editorAssetService;

    public EditorAssetController(EditorAssetService editorAssetService) {
        this.editorAssetService = editorAssetService;
    }

    @PostMapping(value = "/editor/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EditorAssetApiModels.EditorImageUploadResponse uploadEditorImage(@RequestPart("file") MultipartFile file) {
        return editorAssetService.uploadEditorImage(file);
    }

    @GetMapping("/public/editor-images/{fileName}")
    public ResponseEntity<Resource> getEditorImage(@PathVariable String fileName) {
        Resource resource = editorAssetService.loadEditorImage(fileName);
        MediaType mediaType = MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic())
                .contentType(mediaType)
                .body(resource);
    }
}


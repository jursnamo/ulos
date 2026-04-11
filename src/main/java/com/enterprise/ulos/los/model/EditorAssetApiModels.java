package com.enterprise.ulos.los.model;

import java.util.List;

public final class EditorAssetApiModels {

    private EditorAssetApiModels() {
    }

    public record EditorImageUploadResponse(
            String location,
            String fileName,
            long sizeBytes,
            String extension,
            int maxSizeMb,
            List<String> allowedExtensions
    ) {
    }
}


package com.enterprise.ulos.los.model;

import java.util.List;

public final class SystemApiModels {

    private SystemApiModels() {
    }

    public record RouteItem(
            String method,
            String path,
            String description
    ) {
    }

    public record RouteCatalogResponse(
            List<RouteItem> routes
    ) {
    }
}

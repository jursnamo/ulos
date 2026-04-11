package com.enterprise.ulos.los.controller;

import com.enterprise.ulos.los.model.SystemApiModels;
import com.enterprise.ulos.los.service.SystemRoutesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    private final SystemRoutesService systemRoutesService;

    public SystemController(SystemRoutesService systemRoutesService) {
        this.systemRoutesService = systemRoutesService;
    }

    @GetMapping("/routes")
    public SystemApiModels.RouteCatalogResponse routes() {
        return systemRoutesService.routes();
    }
}

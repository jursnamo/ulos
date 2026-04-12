package com.enterprise.ulos.los.service;

import com.enterprise.ulos.los.model.SystemApiModels;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemRoutesService {

    public SystemApiModels.RouteCatalogResponse routes() {
        return new SystemApiModels.RouteCatalogResponse(List.of(
                new SystemApiModels.RouteItem("POST", "/api/auth/login", "Login and get access token"),
                new SystemApiModels.RouteItem("GET", "/api/auth/me", "Get active user profile"),
                new SystemApiModels.RouteItem("POST", "/api/auth/logout", "Revoke current token"),
                new SystemApiModels.RouteItem("GET", "/api/system/routes", "List API route catalog"),
                new SystemApiModels.RouteItem("GET", "/api/dashboard/summary", "LOS dashboard summary"),
                new SystemApiModels.RouteItem("GET", "/api/customers", "List customer portfolio"),
                new SystemApiModels.RouteItem("GET", "/api/customers/page", "Paged customer portfolio list with search"),
                new SystemApiModels.RouteItem("GET", "/api/customers/{cifNumber}", "Get customer detail"),
                new SystemApiModels.RouteItem("POST", "/api/customers", "Create or update customer"),
                new SystemApiModels.RouteItem("GET", "/api/applications", "List applications"),
                new SystemApiModels.RouteItem("GET", "/api/applications/{applicationId}", "Get application workspace"),
                new SystemApiModels.RouteItem("POST", "/api/applications", "Save application draft"),
                new SystemApiModels.RouteItem("POST", "/api/applications/{applicationId}/submit", "Submit application to Flowable workflow"),
                new SystemApiModels.RouteItem("GET", "/api/prechecks", "Paged SLIK/Appraisal pre-check list"),
                new SystemApiModels.RouteItem("GET", "/api/prechecks/{checkRef}", "Get pre-check detail by reference"),
                new SystemApiModels.RouteItem("GET", "/api/prechecks/source/latest-application", "Load third-party/collateral source from latest application"),
                new SystemApiModels.RouteItem("POST", "/api/prechecks/request", "Request new SLIK/Appraisal check to surrounding system"),
                new SystemApiModels.RouteItem("POST", "/api/prechecks", "Create/update SLIK/Appraisal pre-check"),
                new SystemApiModels.RouteItem("GET", "/api/approvals/tasks", "List approval tasks"),
                new SystemApiModels.RouteItem("POST", "/api/approvals/tasks/{taskId}/complete", "Complete approval task"),
                new SystemApiModels.RouteItem("GET", "/api/master/products", "Master product list"),
                new SystemApiModels.RouteItem("GET", "/api/master/facilities", "Master facility list"),
                new SystemApiModels.RouteItem("GET", "/api/master/portfolio-lookups", "Portfolio form master lookup list"),
                new SystemApiModels.RouteItem("GET", "/api/master/portfolio-lookups/page", "Paged portfolio lookup list with search"),
                new SystemApiModels.RouteItem("POST", "/api/master/portfolio-lookups", "Create/update portfolio master lookup item"),
                new SystemApiModels.RouteItem("DELETE", "/api/master/portfolio-lookups/{masterType}/{code}", "Delete portfolio master lookup item"),
                new SystemApiModels.RouteItem("POST", "/api/editor/upload-image", "Upload TinyMCE image with master-policy validation"),
                new SystemApiModels.RouteItem("GET", "/api/public/editor-images/{fileName}", "Serve TinyMCE uploaded image"),
                new SystemApiModels.RouteItem("GET", "/api/bpmn", "List BPMN versions"),
                new SystemApiModels.RouteItem("GET", "/api/bpmn/{processKey}", "Get latest BPMN XML by process key"),
                new SystemApiModels.RouteItem("POST", "/api/bpmn/deploy", "Deploy BPMN XML as new Flowable version"),
                new SystemApiModels.RouteItem("GET", "/api/admin/users", "List users and roles"),
                new SystemApiModels.RouteItem("POST", "/api/admin/users", "Create user"),
                new SystemApiModels.RouteItem("PUT", "/api/admin/users/{userId}", "Update user"),
                new SystemApiModels.RouteItem("PUT", "/api/admin/users/{userId}/password", "Reset user password")
        ));
    }
}

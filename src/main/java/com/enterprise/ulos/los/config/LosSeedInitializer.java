package com.enterprise.ulos.los.config;

import com.enterprise.ulos.domain.bpmn.BpmnModelEntity;
import com.enterprise.ulos.los.entity.ApplicationWorkspaceEntity;
import com.enterprise.ulos.los.repository.ApplicationWorkspaceRepository;
import com.enterprise.ulos.repository.BpmnModelRepository;
import com.enterprise.ulos.los.service.CorporateLosWorkflowService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class LosSeedInitializer {

    @Bean
    CommandLineRunner losBootstrap(
            ApplicationWorkspaceRepository applicationWorkspaceRepository,
            CorporateLosWorkflowService corporateLosWorkflowService,
            BpmnModelRepository bpmnModelRepository,
            RepositoryService repositoryService
    ) {
        return args -> {
            applicationWorkspaceRepository.findByApplicationId("APP-2024-001")
                    .filter(application -> application.getProcessInstanceId() == null || application.getProcessInstanceId().isBlank())
                    .filter(application -> "DRAFT".equalsIgnoreCase(application.getWorkflowStatus()))
                    .ifPresent(application -> corporateLosWorkflowService.launch(application.getApplicationId()));

            snapshotBpmn(bpmnModelRepository, repositoryService, "corporateLosApproval", "System bootstrap");
            snapshotBpmn(bpmnModelRepository, repositoryService, "loanApproval", "System bootstrap");
        };
    }

    private void snapshotBpmn(
            BpmnModelRepository bpmnModelRepository,
            RepositoryService repositoryService,
            String processKey,
            String deployedBy
    ) throws Exception {
        if (bpmnModelRepository.findTopByProcessKeyOrderByVersionDesc(processKey).isPresent()) {
            return;
        }

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .latestVersion()
                .singleResult();
        if (processDefinition == null) {
            return;
        }

        try (InputStream input = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName())) {
            if (input == null) {
                return;
            }
            BpmnModelEntity entity = new BpmnModelEntity();
            entity.setProcessKey(processDefinition.getKey());
            entity.setProcessName(processDefinition.getName());
            entity.setResourceName(processDefinition.getResourceName());
            entity.setBpmnXml(new String(input.readAllBytes(), StandardCharsets.UTF_8));
            entity.setDeploymentId(processDefinition.getDeploymentId());
            entity.setProcessDefinitionId(processDefinition.getId());
            entity.setVersion(processDefinition.getVersion());
            entity.setActive(true);
            entity.setDeployedBy(deployedBy);
            entity.setChangeSummary("Initial BPMN registry snapshot");
            bpmnModelRepository.save(entity);
        }
    }
}

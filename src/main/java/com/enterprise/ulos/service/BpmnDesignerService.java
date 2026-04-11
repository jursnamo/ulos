package com.enterprise.ulos.service;

import com.enterprise.ulos.domain.bpmn.BpmnDeployRequest;
import com.enterprise.ulos.domain.bpmn.BpmnDeployResponse;
import com.enterprise.ulos.domain.bpmn.BpmnModelEntity;
import com.enterprise.ulos.domain.bpmn.BpmnXmlResponse;
import com.enterprise.ulos.los.model.BpmnApiModels;
import com.enterprise.ulos.repository.BpmnModelRepository;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Transactional
public class BpmnDesignerService {

    private final RepositoryService repositoryService;
    private final BpmnModelRepository bpmnModelRepository;

    public BpmnDesignerService(RepositoryService repositoryService, BpmnModelRepository bpmnModelRepository) {
        this.repositoryService = repositoryService;
        this.bpmnModelRepository = bpmnModelRepository;
    }

    public BpmnXmlResponse getProcessXml(String processKey) {
        BpmnModelEntity storedModel = bpmnModelRepository.findTopByProcessKeyOrderByVersionDesc(processKey).orElse(null);
        if (storedModel != null) {
            return new BpmnXmlResponse(
                    storedModel.getProcessKey(),
                    storedModel.getProcessDefinitionId(),
                    storedModel.getVersion(),
                    storedModel.getResourceName(),
                    storedModel.getBpmnXml(),
                    storedModel.getDeployedBy(),
                    storedModel.getChangeSummary()
            );
        }

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .latestVersion()
                .singleResult();

        if (processDefinition == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Process definition not found: " + processKey);
        }

        try (InputStream inputStream = repositoryService.getResourceAsStream(
                processDefinition.getDeploymentId(),
                processDefinition.getResourceName()
        )) {
            if (inputStream == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "BPMN resource not found for: " + processKey);
            }

            String bpmnXml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return new BpmnXmlResponse(
                    processDefinition.getKey(),
                    processDefinition.getId(),
                    processDefinition.getVersion(),
                    processDefinition.getResourceName(),
                    bpmnXml,
                    null,
                    null
            );
        } catch (IOException exception) {
            throw new UncheckedIOException("Failed to read BPMN XML for process: " + processKey, exception);
        }
    }

    @Transactional(readOnly = true)
    public List<BpmnApiModels.BpmnVersionResponse> listVersions() {
        return bpmnModelRepository.findAllByOrderByProcessKeyAscVersionDesc().stream()
                .map(model -> new BpmnApiModels.BpmnVersionResponse(
                        model.getId(),
                        model.getProcessKey(),
                        model.getProcessName(),
                        model.getResourceName(),
                        model.getProcessDefinitionId(),
                        model.getDeploymentId(),
                        model.getVersion(),
                        model.isActive(),
                        model.getDeployedBy(),
                        model.getChangeSummary(),
                        model.getCreatedAt(),
                        model.getUpdatedAt()
                ))
                .toList();
    }

    public BpmnDeployResponse deployProcess(BpmnDeployRequest request) {
        validateRequest(request);

        Deployment deployment = repositoryService.createDeployment()
                .name(request.processName())
                .addString(resolveResourceName(request.resourceName()), request.bpmnXml())
                .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .latestVersion()
                .singleResult();

        if (processDefinition == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Deployment succeeded but no process definition was created"
            );
        }

        deactivatePreviousModels(processDefinition.getKey());
        saveModel(request, deployment, processDefinition);

        return new BpmnDeployResponse(
                deployment.getId(),
                deployment.getName(),
                processDefinition.getId(),
                processDefinition.getKey(),
                processDefinition.getVersion(),
                processDefinition.getResourceName(),
                request.deployedBy(),
                request.changeSummary()
        );
    }

    private void saveModel(BpmnDeployRequest request, Deployment deployment, ProcessDefinition processDefinition) {
        BpmnModelEntity model = new BpmnModelEntity();
        model.setProcessKey(processDefinition.getKey());
        model.setProcessName(request.processName());
        model.setResourceName(processDefinition.getResourceName());
        model.setBpmnXml(request.bpmnXml());
        model.setDeploymentId(deployment.getId());
        model.setProcessDefinitionId(processDefinition.getId());
        model.setVersion(processDefinition.getVersion());
        model.setActive(true);
        model.setDeployedBy(request.deployedBy());
        model.setChangeSummary(request.changeSummary());
        bpmnModelRepository.save(model);
    }

    private void deactivatePreviousModels(String processKey) {
        bpmnModelRepository.findByProcessKeyOrderByVersionDesc(processKey)
                .forEach(model -> model.setActive(false));
    }

    private void validateRequest(BpmnDeployRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        }
        if (isBlank(request.processName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "processName is required");
        }
        if (isBlank(request.bpmnXml())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bpmnXml is required");
        }
        if (!isBlank(request.processKey()) && !request.bpmnXml().contains(request.processKey())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "processKey does not match BPMN XML payload");
        }
    }

    private String resolveResourceName(String resourceName) {
        if (isBlank(resourceName)) {
            return "process.bpmn20.xml";
        }
        return resourceName.endsWith(".bpmn20.xml") ? resourceName : resourceName + ".bpmn20.xml";
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

package com.enterprise.ulos.workflow;


import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class WorkflowService {
    private final RuntimeService runtimeService;
    private final TaskService taskService;
    
    public WorkflowService(RuntimeService runtimeService,
                           TaskService taskService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    public ProcessInstance startProcess(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
        return runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
    }

    public List<com.enterprise.ulos.domain.workflow.WorkflowTaskResponse> getTasks() {
        return taskService.createTaskQuery()
                .list()
                .stream()
                .map(this::toTaskResponse)
                .toList();
    }

    public void completeTask(String taskId, Map<String, Object> variables) {
        taskService.complete(taskId, variables);
    }

    public String getProcessInstanceIdForTask(String taskId) {
        return getTask(taskId).getProcessInstanceId();
    }

    private Task getTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found: " + taskId);
        }
        return task;
    }

    private com.enterprise.ulos.domain.workflow.WorkflowTaskResponse toTaskResponse(Task task) {
        return new com.enterprise.ulos.domain.workflow.WorkflowTaskResponse(
                task.getId(),
                task.getName(),
                task.getAssignee(),
                task.getProcessInstanceId(),
                task.getProcessDefinitionId()
        );
    }
}

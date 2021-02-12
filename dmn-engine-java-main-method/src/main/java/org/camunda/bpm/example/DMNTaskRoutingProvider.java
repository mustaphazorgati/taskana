package org.camunda.bpm.example;

import java.io.IOException;
import java.io.InputStream;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.spi.routing.api.TaskRoutingProvider;
import pro.taskana.task.api.models.Task;

public class DMNTaskRoutingProvider implements TaskRoutingProvider {

  private TaskanaEngine taskanaEngine;
  private DmnEngine dmnEngine;
  private DmnDecision table;

  @Override
  public void initialize(TaskanaEngine taskanaEngine) {
    this.taskanaEngine = taskanaEngine;
    dmnEngine =
        DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();
    try (InputStream inputStream = DMNTaskRoutingProvider.class.getResourceAsStream("dmn-table.dmn")) {
      // parse decision from resource input stream
      table = dmnEngine.parseDecision("example", inputStream);
    } catch (IOException e) {
      throw new SystemException("Could not find file 'dmn-table.dmn'");
    }
  }

  @Override
  public String determineWorkbasketId(Task task) {
    VariableMap variables = Variables
        .putValue("classificationName", task.getClassificationSummary().getName());

    // evaluate decision
    DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(table, variables);

    if (result.getSingleResult() == null) {
      return null;
    }

    // print result
    String workbasketKey = result.getSingleResult().getEntry("workbasketKey");
    String domain = result.getSingleResult().getEntry("domain");

    try {
      return taskanaEngine.getWorkbasketService().getWorkbasket(workbasketKey, domain).getId();
    } catch (Exception e) {
      throw new SystemException(String.format("Unknown workbasket defined in DMN Table. key: '%s', domain: '%s'", workbasketKey, domain));
    }
  }
}

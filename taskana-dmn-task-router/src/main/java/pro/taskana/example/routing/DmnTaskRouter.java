package pro.taskana.example.routing;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.OutputEntry;
import org.camunda.bpm.model.dmn.instance.Rule;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.spi.routing.api.TaskRoutingProvider;
import pro.taskana.task.api.models.Task;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

public class DmnTaskRouter implements TaskRoutingProvider {

  private TaskanaEngine taskanaEngine;
  private DmnEngine dmnEngine;
  private DmnDecision decision;

  @Override
  public void initialize(TaskanaEngine taskanaEngine) {
    this.taskanaEngine = taskanaEngine;
    dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();
    DmnModelInstance dmnModel;
    try (InputStream inputStream = DmnTaskRouter.class.getResourceAsStream("dmn-table.dmn")) {
      dmnModel = Dmn.readModelFromStream(inputStream);
    } catch (IOException e) {
      throw new SystemException("Could not find file 'dmn-table.dmn'");
    }
    decision = dmnEngine.parseDecision("workbasketRouting", dmnModel);

    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();

    Set<Pair<String, String>> allWorkbaskets = new HashSet<>();

    for (Rule rule : dmnModel.getModelElementsByType(Rule.class)) {
      String workbasketKey = "";
      String domain = "";
      Collection<OutputEntry> outputEntries = rule.getOutputEntries();
      for (OutputEntry entry : outputEntries) {
        if (entry.getId().endsWith("workbasketKey")) {
          workbasketKey = entry.getTextContent();
        }
        if (entry.getId().endsWith("domain")) {
          domain = entry.getTextContent();
        }
      }
      allWorkbaskets.add(Pair.of(workbasketKey, domain));
    }

    InternalTaskanaEngine internalTaskanaEngine;
    try {
      Field internal = TaskanaEngineImpl.class.getDeclaredField("internalTaskanaEngineImpl");
      internal.setAccessible(true);
      internalTaskanaEngine = (InternalTaskanaEngine) internal.get(taskanaEngine);
    } catch (Exception e) {
      throw new SystemException("Could not access internal taskana engine");
    }
    for (Pair<String, String> pair : allWorkbaskets) {
      String workbasketKey = pair.getLeft().replace("\"", "");
      String domain = pair.getRight().replace("\"", "");
      // This can be replaced with a workbasketQuery call.
      // Unfortunately the WorkbasketQuery does not support a keyDomainIn operation.
      // Therefore we fetch every workbasket separately
      internalTaskanaEngine.runAsAdmin(
          () -> {
            try {
              return workbasketService.getWorkbasket(workbasketKey, domain);
            } catch (Exception e) {
              throw new SystemException(
                  String.format(
                      "Unknown workbasket defined in DMN Table. key: '%s', domain: '%s'",
                      workbasketKey, domain),
                  e);
            }
          });
    }
  }

  @Override
  public String determineWorkbasketId(Task task) {
    VariableMap variables = Variables.putValue("task", task);

    // evaluate decision
    DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, variables);

    if (result.getSingleResult() == null) {
      return null;
    }

    // print result
    String workbasketKey = result.getSingleResult().getEntry("workbasketKey");
    String domain = result.getSingleResult().getEntry("domain");

    try {
      return taskanaEngine.getWorkbasketService().getWorkbasket(workbasketKey, domain).getId();
    } catch (WorkbasketNotFoundException e) {
      throw new SystemException(
          String.format(
              "Unknown workbasket defined in DMN Table. key: '%s', domain: '%s'",
              workbasketKey, domain));
    } catch (NotAuthorizedException e) {
      throw new SystemException(
          String.format(
              "The current user is not authorized to create a task in the routed workbasket. "
                  + "key: '%s', domain: '%s'",
              workbasketKey, domain));
    }
  }
}

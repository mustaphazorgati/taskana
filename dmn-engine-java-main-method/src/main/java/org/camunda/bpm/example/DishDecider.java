package org.camunda.bpm.example;

import java.io.IOException;
import java.io.InputStream;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;

public class DishDecider {

  public static void main(String[] args) throws IOException {
    String classificationName = "Widerspruch";

    // prepare variables for decision evaluation
    VariableMap variables =
        Variables.putValue("classificationName", classificationName);

    // create a new default DMN engine
    DmnEngine dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();

    try (InputStream inputStream = DishDecider.class.getResourceAsStream("dmn-table.dmn")) {
      // parse decision from resource input stream
      DmnDecision decision = dmnEngine.parseDecision("example", inputStream);

      // evaluate decision
      DmnDecisionTableResult result = dmnEngine.evaluateDecisionTable(decision, variables);

      // print result
      // TODO: prevent NPE
      String workbasketKey = result.getSingleResult().getEntry("workbasketKey");
      String domain = result.getSingleResult().getEntry("domain");
      System.out.println("WorkbkasketKey: " + workbasketKey);
      System.out.println("domain: " + domain);
    }
  }
  
}

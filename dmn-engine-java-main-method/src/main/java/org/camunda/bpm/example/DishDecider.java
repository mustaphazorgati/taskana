/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

  public static void main(String[] args) {
    String classificationName = "Widerspruch";

    // prepare variables for decision evaluation
    VariableMap variables =
        Variables.putValue("classificationName", classificationName);

    // create a new default DMN engine
    DmnEngine dmnEngine = DmnEngineConfiguration.createDefaultDmnEngineConfiguration().buildEngine();

    // parse decision from resource input stream
    InputStream inputStream = DishDecider.class.getResourceAsStream("dmn-table.dmn");

    try {
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
    finally {
      try {
        inputStream.close();
      }
      catch (IOException e) {
        System.err.println("Could not close stream: "+e.getMessage());
      }
    }
  }


}

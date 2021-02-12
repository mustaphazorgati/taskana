package pro.taskana.example.decision;

import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.Decision;
import org.camunda.bpm.model.dmn.instance.DecisionTable;
import org.camunda.bpm.model.dmn.instance.Definitions;
import org.camunda.bpm.model.dmn.instance.Input;
import org.camunda.bpm.model.dmn.instance.InputExpression;
import org.camunda.bpm.model.dmn.instance.Output;
import org.camunda.bpm.model.dmn.instance.Text;

public class DmnBuilder {

  private final DmnModelInstance modelInstance;
  private final Definitions definitions;
  private DecisionTable currentDecisionTable;

  public DmnBuilder() {
    modelInstance = Dmn.createEmptyModel();

    definitions = modelInstance.newInstance(Definitions.class);
    definitions.setNamespace("http://camunda.org/schema/1.0/dmn");
    definitions.setName("definitions");
    definitions.setId("definitions");
    modelInstance.setDefinitions(definitions);
  }

  public DmnBuilder decisionTable(String id, String name) {
    Decision decision = modelInstance.newInstance(Decision.class);
    decision.setId(id);
    decision.setName(name);
    definitions.addChildElement(decision);

    currentDecisionTable = modelInstance.newInstance(DecisionTable.class);
    currentDecisionTable.setId(id + "_decisionTable");
    decision.addChildElement(currentDecisionTable);
    return this;
  }

  public DmnBuilder input(String id, String typeRef, String expression, String label) {
    if (currentDecisionTable == null) {
      throw new IllegalStateException("Can't create an input if no decision table was created!");
    }

    Text text = modelInstance.newInstance(Text.class);
    text.setTextContent(expression);

    InputExpression inputExpression = modelInstance.newInstance(InputExpression.class);
    inputExpression.setId(currentDecisionTable.getId() + "-inputExpression_" + id);
    inputExpression.setTypeRef(typeRef);
    inputExpression.setText(text);

    Input input = modelInstance.newInstance(Input.class);
    input.setId(currentDecisionTable.getId() + "-input_" + id);
    input.setLabel(label);
    input.addChildElement(inputExpression);

    currentDecisionTable.addChildElement(input);

    return this;
  }

  public DmnBuilder output(String typeRef, String name, String label) {
    if (currentDecisionTable == null) {
      throw new IllegalStateException("Can't create an output if no decision table was created!");
    }

    Output output = modelInstance.newInstance(Output.class);
    output.setId(currentDecisionTable.getId() + "-output_" + name);
    output.setLabel(label);
    output.setName(name);
    output.setTypeRef(typeRef);
    currentDecisionTable.addChildElement(output);

    return this;
  }

  public DmnModelInstance build() {
    return modelInstance;
  }
}

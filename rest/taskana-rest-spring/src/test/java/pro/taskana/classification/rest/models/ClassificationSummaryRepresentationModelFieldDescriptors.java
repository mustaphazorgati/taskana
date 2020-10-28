package pro.taskana.classification.rest.models;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.restdocs.payload.FieldDescriptor;

import pro.taskana.common.internal.util.Pair;

public class ClassificationSummaryRepresentationModelFieldDescriptors {

  public static final List<FieldDescriptor> FIELDS;

  static {
    FIELDS =
        Stream.of(
                Pair.of("classificationId", "Unique Id"),
                Pair.of(
                    "key",
                    "The key of the classification. This is typically an externally "
                        + "known code or abbreviation of the classification"),
                Pair.of(
                    "applicationEntryPoint",
                    "The logical name of the entry point, the task list application "
                        + "should redirect to work on a task of this classification"),
                Pair.of(
                    "category",
                    "The category of the classification (MANUAL, EXTERNAL, AUTOMATIC, PROCESS)"),
                Pair.of("domain", "The domain for which this classification is specified"),
                Pair.of("name", "The name of the classification"),
                Pair.of(
                    "parentId",
                    "The id of the parent classification. Empty string (\"\") "
                        + "if this is a root classification."),
                Pair.of(
                    "parentKey",
                    "The key of the parent classification. Empty string (\"\") "
                        + "if this is a root classification."),
                Pair.of("priority", "The priority of the classification"),
                Pair.of(
                    "serviceLevel",
                    "The service level of the classification. "
                        + "This is stated according to ISO 8601"),
                Pair.of("type", "The type of classification (TASK, DOCUMENT)"),
                Pair.of("custom1", "A custom property with name \"1\""),
                Pair.of("custom2", "A custom property with name \"2\""),
                Pair.of("custom3", "A custom property with name \"3\""),
                Pair.of("custom4", "A custom property with name \"4\""),
                Pair.of("custom5", "A custom property with name \"5\""),
                Pair.of("custom6", "A custom property with name \"6\""),
                Pair.of("custom7", "A custom property with name \"7\""),
                Pair.of("custom8", "A custom property with name \"8\""))
            .map(p -> fieldWithPath(p.getLeft()).description(p.getRight()))
            .collect(Collectors.toList());
  }
}

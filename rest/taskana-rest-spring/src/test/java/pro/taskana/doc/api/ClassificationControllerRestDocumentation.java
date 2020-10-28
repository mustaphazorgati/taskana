package pro.taskana.doc.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModelFieldDescriptors;
import pro.taskana.common.rest.Mapping;
import pro.taskana.common.rest.models.PagingFieldDescriptors;
import pro.taskana.common.test.doc.api.BaseRestDocumentation;
import pro.taskana.common.test.rest.RestHelper;

/** Generate REST Dokumentation for ClassificationController. */
class ClassificationControllerRestDocumentation extends BaseRestDocumentation {

  private final HashMap<String, String> classificationFieldDescriptionsMap = new HashMap<>();

  private FieldDescriptor[] allClassificationsFieldDescriptors;
  private FieldDescriptor[] classificationFieldDescriptors;
  private FieldDescriptor[] classificationSubsetFieldDescriptors;
  private FieldDescriptor[] createClassificationFieldDescriptors;

  @BeforeEach
  void setUp() {

    classificationFieldDescriptionsMap.put("classificationId", "Unique Id");
    classificationFieldDescriptionsMap.put(
        "key",
        "The key of the classification. This is typically an externally "
            + "known code or abbreviation of the classification");
    classificationFieldDescriptionsMap.put(
        "parentId",
        "The id of the parent classification. Empty string (\"\") "
            + "if this is a root classification.");
    classificationFieldDescriptionsMap.put(
        "parentKey",
        "The key of the parent classification. Empty string (\"\") "
            + "if this is a root classification.");
    classificationFieldDescriptionsMap.put(
        "category", "The category of the classification (MANUAL, EXTERNAL, AUTOMATIC, PROCESS)");
    classificationFieldDescriptionsMap.put("type", "The type of classification (TASK, DOCUMENT)");
    classificationFieldDescriptionsMap.put(
        "domain", "The domain for which this classification is specified");
    classificationFieldDescriptionsMap.put(
        "isValidInDomain", "True, if this classification to objects in this domain");
    classificationFieldDescriptionsMap.put(
        "created", "The creation timestamp of the classification in the system");
    classificationFieldDescriptionsMap.put(
        "modified", "The timestamp of the last modification date");
    classificationFieldDescriptionsMap.put("name", "The name of the classification");
    classificationFieldDescriptionsMap.put("description", "The description of the classification");
    classificationFieldDescriptionsMap.put("priority", "The priority of the classification");
    classificationFieldDescriptionsMap.put(
        "serviceLevel",
        "The service level of the classification. This is stated according to ISO 8601");
    classificationFieldDescriptionsMap.put(
        "applicationEntryPoint",
        "The logical name of the entry point, the task list application "
            + "should redirect to work on a task of this classification");
    classificationFieldDescriptionsMap.put("custom1", "A custom property with name \"1\"");
    classificationFieldDescriptionsMap.put("custom2", "A custom property with name \"2\"");
    classificationFieldDescriptionsMap.put("custom3", "A custom property with name \"3\"");
    classificationFieldDescriptionsMap.put("custom4", "A custom property with name \"4\"");
    classificationFieldDescriptionsMap.put("custom5", "A custom property with name \"5\"");
    classificationFieldDescriptionsMap.put("custom6", "A custom property with name \"6\"");
    classificationFieldDescriptionsMap.put("custom7", "A custom property with name \"7\"");
    classificationFieldDescriptionsMap.put("custom8", "A custom property with name \"8\"");
    classificationFieldDescriptionsMap.put(
        "_links.getAllClassifications.href", "Link to all classifications");
    classificationFieldDescriptionsMap.put("_links.getAllClassifications.templated", "");

    allClassificationsFieldDescriptors =
        new FieldDescriptor[] {
          subsectionWithPath("classifications")
              .description("An Array of <<classification-subset, Classification-Subsets>>")
        };

    classificationFieldDescriptors =
        new FieldDescriptor[] {fieldWithPath("_links.self.href").ignored()};

    classificationSubsetFieldDescriptors =
        new FieldDescriptor[] {
          fieldWithPath("classificationId")
              .description(classificationFieldDescriptionsMap.get("classificationId")),
          fieldWithPath("key").description(classificationFieldDescriptionsMap.get("key")),
          fieldWithPath("category").description(classificationFieldDescriptionsMap.get("category")),
          fieldWithPath("type").description(classificationFieldDescriptionsMap.get("type")),
          fieldWithPath("domain").description(classificationFieldDescriptionsMap.get("domain")),
          fieldWithPath("isValidInDomain").ignored(),
          fieldWithPath("created").ignored(),
          fieldWithPath("modified").ignored(),
          fieldWithPath("name").description(classificationFieldDescriptionsMap.get("name")),
          fieldWithPath("parentId").description(classificationFieldDescriptionsMap.get("parentId")),
          fieldWithPath("parentKey")
              .description(classificationFieldDescriptionsMap.get("parentKey")),
          fieldWithPath("description").ignored(),
          fieldWithPath("priority").description(classificationFieldDescriptionsMap.get("priority")),
          fieldWithPath("serviceLevel")
              .description(classificationFieldDescriptionsMap.get("serviceLevel")),
          fieldWithPath("applicationEntryPoint").ignored(),
          fieldWithPath("custom1").description(classificationFieldDescriptionsMap.get("custom1")),
          fieldWithPath("custom2").description(classificationFieldDescriptionsMap.get("custom2")),
          fieldWithPath("custom3").description(classificationFieldDescriptionsMap.get("custom3")),
          fieldWithPath("custom4").description(classificationFieldDescriptionsMap.get("custom4")),
          fieldWithPath("custom5").description(classificationFieldDescriptionsMap.get("custom5")),
          fieldWithPath("custom6").description(classificationFieldDescriptionsMap.get("custom6")),
          fieldWithPath("custom7").description(classificationFieldDescriptionsMap.get("custom7")),
          fieldWithPath("custom8").description(classificationFieldDescriptionsMap.get("custom8")),
          fieldWithPath("_links.self.href").ignored()
        };

    createClassificationFieldDescriptors =
        new FieldDescriptor[] {
          fieldWithPath("category")
              .type("String")
              .description(
                  "The category of the classification (MANUAL, EXTERNAL, AUTOMATIC, PROCESS)")
              .optional(),
          fieldWithPath("domain")
              .description("The domain for which this classification is specified"),
          fieldWithPath("key")
              .description(
                  "The key of the classification. This is typically an externally "
                      + "known code or abbreviation of the classification"),
          fieldWithPath("name")
              .type("String")
              .description("The name of the classification")
              .optional(),
          fieldWithPath("type")
              .type("String")
              .description("The type of classification (TASK, DOCUMENT)")
              .optional(),
          fieldWithPath("parentId")
              .type("String")
              .description(classificationFieldDescriptionsMap.get("parentId"))
              .optional(),
          fieldWithPath("parentKey")
              .type("String")
              .description(classificationFieldDescriptionsMap.get("parentKey"))
              .optional(),
          fieldWithPath("isValidInDomain")
              .type("Boolean")
              .description(classificationFieldDescriptionsMap.get("isValidInDomain"))
              .optional(),
          fieldWithPath("created")
              .type("String")
              .description(classificationFieldDescriptionsMap.get("created"))
              .optional(),
          fieldWithPath("modified")
              .type("String")
              .description(classificationFieldDescriptionsMap.get("modified"))
              .optional(),
          fieldWithPath("description")
              .type("String")
              .description(classificationFieldDescriptionsMap.get("description"))
              .optional(),
          fieldWithPath("priority")
              .type("Number")
              .description(classificationFieldDescriptionsMap.get("priority"))
              .optional(),
          fieldWithPath("serviceLevel")
              .type("String")
              .description(classificationFieldDescriptionsMap.get("serviceLevel"))
              .optional(),
          fieldWithPath("applicationEntryPoint")
              .type("String")
              .description(classificationFieldDescriptionsMap.get("applicationEntryPoint"))
              .optional(),
          fieldWithPath("custom1")
              .type("String")
              .description(classificationFieldDescriptionsMap.get("custom1"))
              .optional(),
          fieldWithPath("custom2")
              .type("String")
              .description(classificationFieldDescriptionsMap.get("custom2"))
              .optional(),
          fieldWithPath("custom3")
              .type("String")
              .description(classificationFieldDescriptionsMap.get("custom3"))
              .optional(),
          fieldWithPath("custom4")
              .type("String")
              .description(classificationFieldDescriptionsMap.get("custom4"))
              .optional(),
          fieldWithPath("custom5")
              .type("String")
              .description(classificationFieldDescriptionsMap.get("custom5"))
              .optional(),
          fieldWithPath("custom6")
              .type("String")
              .description(classificationFieldDescriptionsMap.get("custom6"))
              .optional(),
          fieldWithPath("custom7")
              .type("String")
              .description(classificationFieldDescriptionsMap.get("custom7"))
              .optional(),
          fieldWithPath("custom8")
              .type("String")
              .description(classificationFieldDescriptionsMap.get("custom8"))
              .optional()
        };
  }

  @Test
  void getAllClassificationsDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    Mapping.URL_CLASSIFICATIONS + "?domain=DOMAIN_B")
                .headers(restHelper.getHeadersTeamLead1()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            document(
                "GetAllClassificationsDocTest",
                PagingFieldDescriptors.PAGING_LINKS,
                requestParameters(
                    parameterWithName("domain").description("this is a test").optional(),
                    parameterWithName("test").description("bla bla").optional()),
                responseFields(allClassificationsFieldDescriptors).and(subsectionWithPath("_links").ignored())));
  }

  @Test
  void getSpecificClassificationDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(
                        Mapping.URL_CLASSIFICATIONS_ID, "CLI:100000000000000000000000000000000009"))
                .headers(restHelper.getHeadersTeamLead1()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            document(
                "GetSpecificClassificationDocTest",
                responseFields(
                    merge(
                        Arrays.asList(classificationFieldDescriptors),
                        ClassificationSummaryRepresentationModelFieldDescriptors.FIELDS))));
  }

  @Test
  void classificationSubsetDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(
                        Mapping.URL_CLASSIFICATIONS_ID, "CLI:100000000000000000000000000000000009"))
                .headers(restHelper.getHeadersTeamLead1()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            document("ClassificationSubset", responseFields(classificationSubsetFieldDescriptors)));
  }

  @Test
  void createAndDeleteClassificationDocTest() throws Exception {
    MvcResult result =
        this.mockMvc
            .perform(
                RestDocumentationRequestBuilders.post(restHelper.toUrl(Mapping.URL_CLASSIFICATIONS))
                    .headers(restHelper.getHeadersTeamLead1())
                    .content("{\"key\":\"Key0815casdgdgh\", \"domain\":\"DOMAIN_B\"}"))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                document(
                    "CreateClassificationDocTest",
                    requestFields(createClassificationFieldDescriptors),
                    responseFields(classificationFieldDescriptors)))
            .andReturn();

    String content = result.getResponse().getContentAsString();
    String newId = content.substring(content.indexOf("CLI:"), content.indexOf("CLI:") + 40);

    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.delete(
                    restHelper.toUrl(Mapping.URL_CLASSIFICATIONS_ID, newId))
                .headers(restHelper.getHeadersTeamLead1()))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andDo(document("DeleteClassificationDocTest"));
  }

  @Test
  void updateClassificationDocTest() throws Exception {
    URL url =
        new URL(
            restHelper.toUrl(
                Mapping.URL_CLASSIFICATIONS_ID, "CLI:100000000000000000000000000000000009"));
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty("Authorization", RestHelper.AUTHORIZATION_TEAM_LEAD_1);
    assertEquals(200, con.getResponseCode());

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), UTF_8));
    String inputLine;
    StringBuilder content = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();
    con.disconnect();
    String modifiedTask = content.toString();

    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.put(
                    restHelper.toUrl(
                        Mapping.URL_CLASSIFICATIONS_ID, "CLI:100000000000000000000000000000000009"))
                .headers(restHelper.getHeadersTeamLead1())
                .content(modifiedTask))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            document(
                "UpdateClassificationDocTest",
                requestFields(classificationFieldDescriptors),
                responseFields(classificationFieldDescriptors)));
  }
}

package pro.taskana.common.test.doc.api;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;

/** Base class for Rest Documentation tests. */
@TaskanaSpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public abstract class BaseRestDocumentation {

  @Autowired protected MockMvc mockMvc;

  @Autowired protected RestHelper restHelper;

  protected void documentTestForGetRequest(String url, FieldDescriptor[] fieldDescriptors)
      throws Exception {
    mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(url).headers(restHelper.getHeadersTeamLead1()))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "{class-name}/{method-name}", responseFields(fieldDescriptors)));
  }

  @SafeVarargs
  protected static <T> List<T> merge(List<? extends T>... lists) {
    return Arrays.stream(lists).flatMap(Collection::stream).collect(Collectors.toList());
  }

  @TestConfiguration
  static class ResultHandlerConfiguration {

    @Bean
    public RestDocsMockMvcConfigurationCustomizer restDocsMockMvcConfigurationCustomizer() {
      return configurer ->
          configurer
              .operationPreprocessors()
              .withRequestDefaults(prettyPrint())
              .withResponseDefaults(prettyPrint());
    }
  }
}

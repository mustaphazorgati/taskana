package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.TaskanaEngineProxyForTest;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.AttachmentMapper;

/**
 * Acceptance test for all "delete task" scenarios.
 */
@ExtendWith(JaasExtension.class)
class DeleteTaskAccTest extends AbstractAccTest {

  DeleteTaskAccTest() {
    super();
  }

  @WithAccessId(user = "user-1-2", groups = "group-1")
  @Test
  void testDeleteSingleTaskNotAuthorized() {

    TaskService taskService = taskanaEngine.getTaskService();
    ThrowingCallable call =
        () -> {
          taskService.deleteTask("TKI:000000000000000000000000000000000037");
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_deleteAttachments_When_MultipleTasksAreDeleted()
      throws NotAuthorizedException, InvalidArgumentException, NoSuchFieldException,
                 IllegalAccessException {

    TaskService taskService = taskanaEngine.getTaskService();

    TaskanaEngineProxyForTest engineProxy = new TaskanaEngineProxyForTest(taskanaEngine);
    AttachmentMapper attachmentMapper =
        engineProxy.getEngine().getSqlSession().getMapper(AttachmentMapper.class);

    assertThat(attachmentMapper.findAttachmentSummariesByTaskIds(Arrays.asList(
        "TKI:000000000000000000000000000000000067", "TKI:000000000000000000000000000000000068")))
        .hasSize(4);

    taskService.deleteTasks(Arrays.asList("TKI:000000000000000000000000000000000067",
        "TKI:000000000000000000000000000000000068"));

    assertThat(attachmentMapper.findAttachmentSummariesByTaskIds(Arrays.asList(
        "TKI:000000000000000000000000000000000067", "TKI:000000000000000000000000000000000068")))
        .hasSize(0);

  }

  @WithAccessId(user = "admin")
  @Test
  void should_deleteAttachments_When_SingleTaskIsDeleted()
      throws NotAuthorizedException, NoSuchFieldException,
                 IllegalAccessException, InvalidStateException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();

    TaskanaEngineProxyForTest engineProxy = new TaskanaEngineProxyForTest(taskanaEngine);
    AttachmentMapper attachmentMapper =
        engineProxy.getSqlSession().getMapper(AttachmentMapper.class);

    assertThat(attachmentMapper.findAttachmentsByTaskId("TKI:000000000000000000000000000000000069"))
        .hasSize(1);

    taskService.deleteTask(
        "TKI:000000000000000000000000000000000069");

    assertThat(attachmentMapper.findAttachmentsByTaskId("TKI:000000000000000000000000000000000069"))
        .hasSize(0);

  }

  @WithAccessId(user = "businessadmin")
  @WithAccessId(user = "taskadmin")
  @WithAccessId(user = "user-1-1")
  @TestTemplate
  void should_ThrowException_When_UserIsNotInAdminRoleButTriesToBulkDeleteTasks() {

    TaskService taskService = taskanaEngine.getTaskService();

    List<String> taskIds =
        Arrays.asList(
            "TKI:000000000000000000000000000000000008",
            "TKI:000000000000000000000000000000000009",
            "TKI:000000000000000000000000000000000008",
            "TKI:000000000000000000000000000000000010");

    ThrowingCallable call =
        () -> {
          taskService.deleteTasks(taskIds);
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(
      user = "user-1-2",
      groups = {"group-1", "admin"})
  @Test
  void testDeleteSingleTask()
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {

    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000036");

    taskService.deleteTask(task.getId());

    ThrowingCallable call =
        () -> {
          taskService.getTask("TKI:000000000000000000000000000000000036");
        };
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "businessadmin")
  @WithAccessId(user = "taskadmin")
  @WithAccessId(user = "user-1-1")
  @TestTemplate
  void should_ThrowException_When_UserIsNotInAdminRole() {

    TaskService taskService = taskanaEngine.getTaskService();

    ThrowingCallable deleteTaskCall =
        () -> {
          taskService.deleteTask("TKI:000000000000000000000000000000000041");
        };
    assertThatThrownBy(deleteTaskCall).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(
      user = "user-1-2",
      groups = {"group-1", "admin"})
  @Test
  void testThrowsExceptionIfTaskIsNotCompleted()
      throws TaskNotFoundException, NotAuthorizedException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000029");

    ThrowingCallable call =
        () -> {
          taskService.deleteTask(task.getId());
        };
    assertThatThrownBy(call).isInstanceOf(InvalidStateException.class);
  }

  @WithAccessId(
      user = "user-1-2",
      groups = {"group-1", "admin"})
  @Test
  void testForceDeleteTaskIfNotCompleted()
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {
    TaskService taskService = taskanaEngine.getTaskService();
    Task task = taskService.getTask("TKI:000000000000000000000000000000000027");

    ThrowingCallable call =
        () -> {
          taskService.deleteTask(task.getId());
        };
    assertThatThrownBy(call)
        .describedAs("Should not be possible to delete claimed task without force flag")
        .isInstanceOf(InvalidStateException.class);

    taskService.forceDeleteTask(task.getId());

    call =
        () -> {
          taskService.getTask("TKI:000000000000000000000000000000000027");
        };
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void testBulkDeleteTask() throws InvalidArgumentException, NotAuthorizedException {

    TaskService taskService = taskanaEngine.getTaskService();
    ArrayList<String> taskIdList = new ArrayList<>();
    taskIdList.add("TKI:000000000000000000000000000000000037");
    taskIdList.add("TKI:000000000000000000000000000000000038");

    BulkOperationResults<String, TaskanaException> results = taskService.deleteTasks(taskIdList);

    assertThat(results.containsErrors()).isFalse();
    ThrowingCallable call =
        () -> {
          taskService.getTask("TKI:000000000000000000000000000000000038");
        };
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }

  @WithAccessId(user = "admin")
  @Test
  void testBulkDeleteTasksWithException()
      throws TaskNotFoundException, InvalidArgumentException, NotAuthorizedException {

    TaskService taskService = taskanaEngine.getTaskService();
    ArrayList<String> taskIdList = new ArrayList<>();
    taskIdList.add("TKI:000000000000000000000000000000000039");
    taskIdList.add("TKI:000000000000000000000000000000000040");
    taskIdList.add("TKI:000000000000000000000000000000000028");

    BulkOperationResults<String, TaskanaException> results = taskService.deleteTasks(taskIdList);

    String expectedFailedId = "TKI:000000000000000000000000000000000028";
    assertThat(results.containsErrors()).isTrue();
    List<String> failedTaskIds = results.getFailedIds();
    assertThat(failedTaskIds).hasSize(1);
    assertThat(failedTaskIds.get(0)).isEqualTo(expectedFailedId);
    assertThat(InvalidStateException.class)
        .isSameAs(results.getErrorMap().get(expectedFailedId).getClass());

    Task notDeletedTask = taskService.getTask("TKI:000000000000000000000000000000000028");
    assertThat(notDeletedTask).isNotNull();
    ThrowingCallable call =
        () -> {
          taskService.getTask("TKI:000000000000000000000000000000000040");
        };
    assertThatThrownBy(call).isInstanceOf(TaskNotFoundException.class);
  }
}

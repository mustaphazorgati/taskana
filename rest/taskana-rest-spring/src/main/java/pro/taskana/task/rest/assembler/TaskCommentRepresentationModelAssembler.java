package pro.taskana.task.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static pro.taskana.common.rest.models.TaskanaPagedModelKeys.TASK_COMMENTS;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.rest.Mapping;
import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.resource.rest.PageLinks;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.internal.models.TaskCommentImpl;
import pro.taskana.task.rest.TaskCommentController;
import pro.taskana.task.rest.models.TaskCommentRepresentationModel;

/** EntityModel assembler for {@link TaskCommentRepresentationModel}. */
@Component
public class TaskCommentRepresentationModelAssembler
    implements RepresentationModelAssembler<TaskComment, TaskCommentRepresentationModel> {

  private final TaskService taskService;

  @Autowired
  public TaskCommentRepresentationModelAssembler(TaskService taskService) {
    this.taskService = taskService;
  }

  @NonNull
  @Override
  public TaskCommentRepresentationModel toModel(@NonNull TaskComment taskComment) {
    TaskCommentRepresentationModel repModel =
        new TaskCommentRepresentationModel();
    repModel.setTaskCommentId(taskComment.getId());
    repModel.setTaskId(taskComment.getTaskId());
    repModel.setTextField(taskComment.getTextField());
    repModel.setCreator(taskComment.getCreator());
    repModel.setCreated(taskComment.getCreated().toString());
    repModel.setModified(taskComment.getModified().toString());
    try {
      repModel.add(
          linkTo(methodOn(TaskCommentController.class).getTaskComment(taskComment.getId()))
              .withSelfRel());
    } catch (Exception e) {
      throw new SystemException("caught unexpected Exception.", e.getCause());
    }
    return repModel;
  }

  public TaskComment toEntityModel(TaskCommentRepresentationModel repModel) {

    TaskCommentImpl taskComment =
        (TaskCommentImpl) taskService.newTaskComment(repModel.getTaskId());
    taskComment.setId(repModel.getTaskCommentId());
    taskComment.setTextField(repModel.getTextField());
    taskComment.setCreator(repModel.getCreator());
    if (repModel.getCreated() != null) {
      taskComment.setCreated(Instant.parse(repModel.getCreated()));
    }
    if (repModel.getModified() != null) {
      taskComment.setModified(Instant.parse(repModel.getModified()));
    }
    return taskComment;
  }

  @PageLinks(Mapping.URL_TASK_COMMENTS)
  public TaskanaPagedModel<TaskCommentRepresentationModel> toPageModel(
      List<TaskComment> taskComments, PageMetadata pageMetadata) {
    return taskComments.stream()
               .map(this::toModel)
               .collect(
                   Collectors.collectingAndThen(
                       Collectors.toList(),
                       list -> new TaskanaPagedModel<>(TASK_COMMENTS, list, pageMetadata)));
  }
}

import { Action, NgxsAfterBootstrap, State, StateContext } from '@ngxs/store';
import { Observable, of } from 'rxjs';
import { take, tap } from 'rxjs/operators';
import { TaskanaDate } from 'app/shared/util/taskana.date';
import { CategoriesResponse,
  ClassificationCategoriesService } from '../../services/classification-categories/classification-categories.service';
import { CreateClassification,
  DeselectClassification,
  GetClassifications,
  RemoveSelectedClassification,
  RestoreSelectedClassification,
  SaveClassification,
  SelectClassification,
  SetActiveAction,
  SetSelectedClassificationType,
  UpdateClassification } from './classification.actions';
import { ClassificationsService } from '../../services/classifications/classifications.service';
import { ACTION } from '../../models/action';
import { DomainService } from '../../services/domain/domain.service';
import {Classification} from "../../models/classification";

class InitializeStore {
  static readonly type = '[ClassificationState] Initializing state';
}

@State<ClassificationStateModel>({ name: 'classification' })
export class ClassificationState implements NgxsAfterBootstrap {
  constructor(
    private categoryService: ClassificationCategoriesService,
    private classificationsService: ClassificationsService,
    private domainService: DomainService
  ) {
  }

  @Action(InitializeStore)
  initializeStore(ctx: StateContext<ClassificationStateModel>): Observable<any> {
    return this.categoryService.getClassificationCategoriesByType().pipe(
      take(1),
      tap(classificationTypes => {
        ctx.patchState({
          classificationTypes,
          classifications: undefined,
          selectedClassificationType: Object.keys(classificationTypes)[0],
        });
      }),
    );
  }

  @Action(SetSelectedClassificationType)
  setSelectedClassificationType(ctx: StateContext<ClassificationStateModel>, action: SetSelectedClassificationType): Observable<null> {
    if (ctx.getState().classificationTypes[action.selectedType]) {
      ctx.patchState({
        selectedClassificationType: action.selectedType,
        selectedClassification: undefined
      });
    }
    return of(null);
  }

  @Action(SelectClassification)
  selectClassification(ctx: StateContext<ClassificationStateModel>, action: SelectClassification): Observable<any | null> {
    if (typeof action.classificationId !== 'undefined') {
      return this.classificationsService.getClassification(action.classificationId).pipe(
        take(1),
        tap(selectedClassification => ctx.patchState({
          selectedClassification,
          action: null
        }))
      );
    }
    return of(null);
  }

  @Action(DeselectClassification)
  deselectClassification(ctx: StateContext<ClassificationStateModel>): Observable<null> {
    ctx.patchState({
      selectedClassification: undefined,
      action: null
    });
    return of(null);
  }

  @Action(GetClassifications)
  getClassifications(ctx: StateContext<ClassificationStateModel>): Observable<any> {
    const { selectedClassificationType } = ctx.getState();
    return this.classificationsService.getClassifications(selectedClassificationType).pipe(
      take(1),
      tap(resource => ctx.patchState({
        classifications: resource.classifications || []
      })),
    );
  }

  @Action(CreateClassification)
  createClassification(ctx: StateContext<ClassificationStateModel>, action: CreateClassification): Observable<any> {
    return this.classificationsService.postClassification(action.classification).pipe(
      take(1), tap(classification => ctx.patchState({
        classifications: [...ctx.getState().classifications, classification],
        selectedClassification: classification,
        action: null
      }))
    );
  }

  @Action(SaveClassification)
  saveClassification(ctx: StateContext<ClassificationStateModel>, action: SaveClassification): Observable<any> {
    return this.classificationsService.putClassification(action.classification).pipe(
      take(1),
      tap(savedClassification => ctx.patchState({
        classifications: ctx.getState().classifications.map(currentClassification => {
          if (currentClassification.classificationId === savedClassification.classificationId) {
            return savedClassification;
          }
          return currentClassification;
        }),
        selectedClassification: savedClassification
      })),
      tap(() => this.classificationsService.getClassifications(
        ctx.getState().selectedClassificationType
      ).subscribe(
        resource => {
          ctx.patchState({
            classifications: resource.classifications
          });
        }
      ))
    );
  }

  @Action(RestoreSelectedClassification)
  restoreSelectedClassification(ctx: StateContext<ClassificationStateModel>, action: RestoreSelectedClassification): Observable<any> {
    return this.classificationsService.getClassification(action.classificationId).pipe(
      take(1),
      tap(selectedClassification => ctx.patchState({ selectedClassification }))
    );
  }

  @Action(SetActiveAction)
  setActiveAction(ctx: StateContext<ClassificationStateModel>, action: SetActiveAction): Observable<null> {
    if (action.action === ACTION.CREATE) {
      // Initialization of a new classification
      const state: ClassificationStateModel = ctx.getState();
      const date = TaskanaDate.getDate();
      const initialClassification: Classification = {
        type: state.selectedClassificationType,
        category: state.classificationTypes[state.selectedClassificationType][0],
        created: date,
        modified: date,
        domain: this.domainService.getSelectedDomainValue(),
        parentId: state.selectedClassification.classificationId,
        parentKey: state.selectedClassification.key,
      };
      ctx.patchState({ selectedClassification: initialClassification, action: action.action });
    } else {
      ctx.patchState({ action: action.action });
    }
    return of(null);
  }

  @Action(RemoveSelectedClassification)
  removeSelectedClassification(ctx: StateContext<ClassificationStateModel>): Observable<any> {
    const sel = ctx.getState().selectedClassification;
    return this.classificationsService.deleteClassification(sel.classificationId).pipe(
      take(1),
      tap(() => {
        const classifications = ctx.getState().classifications.filter(el => el.classificationId !== sel.classificationId);
        ctx.patchState({ selectedClassification: undefined, classifications });
      })
    );
  }

  @Action(UpdateClassification)
  updateClassification(ctx: StateContext<ClassificationStateModel>, action: SaveClassification): Observable<any> {
    return this.classificationsService.putClassification(action.classification).pipe(
      take(1),
      tap(
        classification => {
          const state = ctx.getState();
          let { selectedClassification } = state;
          if (selectedClassification.classificationId === classification.classificationId) {
            selectedClassification = classification;
          }
          ctx.patchState({
            classifications: state.classifications.map(c => {
              if (c.classificationId === classification.classificationId) {
                return classification;
              }
              return c;
            }),
            selectedClassification
          });
        }
      )
    );
  }

  // initialize after Startup service has configured the taskanaRestUrl properly.
  ngxsAfterBootstrap(ctx: StateContext<ClassificationStateModel>): Observable<null> {
    ctx.dispatch(new InitializeStore());
    return of(null);
  }
}

export interface ClassificationStateModel {
  classifications: Classification[],
  selectedClassification: Classification,
  selectedClassificationType: string;
  classificationTypes: CategoriesResponse,
  action: ACTION,
}

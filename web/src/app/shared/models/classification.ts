import { ClassificationSummary } from './classification-summary';

export interface Classification extends ClassificationSummary {
  isValidInDomain?: boolean;
  created?: string; // TODO: make this a Date
  modified?: string; // TODO: make this a Date
  description?: string;
}

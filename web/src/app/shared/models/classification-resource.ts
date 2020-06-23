import { Classification } from './classification';
import { Links } from './links';

export interface ClassificationResource {
  classifications: Classification[];
  _links?: Links;
}

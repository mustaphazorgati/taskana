import { Injectable } from '@angular/core';
import { TreeNodeModel } from '../../models/tree-node';
import { Classification } from '../../models/classification';

@Injectable({
  providedIn: 'root'
})
export class ClassificationTreeService {
  transformToTreeNode(classifications: Classification[]): TreeNodeModel[] {
    const roots = [];
    const children = [];
    classifications.forEach(item => {
      const parent = item.parentId;
      const target = !parent ? roots : (children[parent] || (children[parent] = []));
      target.push({ ...item, children: [] });
    });
    roots.forEach(parent => this.findChildren(parent, children));
    return roots;
  }

  private findChildren(parent: any, children: Array<any>) {
    if (children[parent.classificationId]) {
      parent.children = children[parent.classificationId];
      parent.children.forEach(child => this.findChildren(child, children));
    }
  }
}

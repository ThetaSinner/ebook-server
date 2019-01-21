import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { InfoHostItem } from '../info-host/info-host-item';

@Injectable({
  providedIn: 'root'
})
export class InfoHostDataSourceService {
  private data: InfoHostItem[] = [];
  private dataSource: Subject<InfoHostItem[]> = new Subject();

  constructor() { }

  getItemsStream(): Observable<InfoHostItem[]> {
    return this.dataSource.asObservable();
  }

  replace(infoHostItem: InfoHostItem) {
    let replaced = false;
    this.data = this.data.map(item => {
      if (item.component === infoHostItem.component) {
        replaced = true;
        return infoHostItem;
      }

      return item;
    });
    
    if (!replaced) {
      this.data.push(infoHostItem);
    }

    this.dataSource.next(this.data);
  }
}

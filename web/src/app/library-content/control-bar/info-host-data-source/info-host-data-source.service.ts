import { Injectable } from '@angular/core';
import { InfoHostItem } from '../info-host/info-host-item';
import { LibraryInfoComponent } from '../library-info/library-info.component';
import { Subject, Observable } from 'rxjs';

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

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
    this.data = this.data.filter(item => item.component !== infoHostItem.component);

    this.data.push(infoHostItem);

    console.log('replacing info item with', infoHostItem, 'and emitting', this.data, 'into the stream');

    this.dataSource.next(this.data);
  }
}

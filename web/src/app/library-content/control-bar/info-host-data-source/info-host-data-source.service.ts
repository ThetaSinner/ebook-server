import { Injectable } from '@angular/core';
import { InfoHostItem } from '../info-host/info-host-item';
import { LibraryInfoComponent } from '../library-info/library-info.component';

@Injectable({
  providedIn: 'root'
})
export class InfoHostDataSourceService {

  constructor() { }

  getInfoItems() {
    return [
      new InfoHostItem(LibraryInfoComponent, {libraryName: 'test', numberOfBooks: 10})
    ];
  }
}

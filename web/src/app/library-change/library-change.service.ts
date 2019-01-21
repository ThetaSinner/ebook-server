import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { ChangeTracker } from './change-tracker';

@Injectable({
  providedIn: 'root'
})
export class LibraryChangeService {
  private serviceSupported = false;

  constructor() {
    if (typeof(EventSource) !== 'undefined') {
      this.serviceSupported = true;
    }
  }

  listen(libraryName: string): ChangeTracker {
    if (!this.serviceSupported) {
      return;
    }

    const source = new EventSource(`${environment.mediaServerUrlBase}/events/subscribe/${libraryName}`);

    return new ChangeTracker(source);
  }
}

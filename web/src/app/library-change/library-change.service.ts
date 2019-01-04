import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LibraryChangeService {
  private serviceSupported = false;

  private sources: Array<EventSource> = [];

  constructor() {
    if (typeof(EventSource) !== 'undefined') {
      this.serviceSupported = true;
    }
  }

  listen(libraryName: string) {
    if (!this.serviceSupported) {
      return;
    }

    const source = new EventSource(`${environment.mediaServerUrlBase}/events/subscribe/${libraryName}`);
    source.addEventListener('change', (e) => {
      console.log('change event', e);
    }, false);
    source.onerror = (event) => {
      console.error('error event', event);
    }

    this.sources.push(source);
  }
}

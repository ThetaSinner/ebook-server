import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LibraryDataService {
  constructor(private httpClient: HttpClient) { }
  
  getLibraries() {
    return this.httpClient.get(`${environment.mediaServerUrlBase}/libraries`);
  }

  createLibrary(libraryName: string) {
    const params = { params: new HttpParams().set('name', libraryName) }

    return this.httpClient.post(`${environment.mediaServerUrlBase}/libraries`, null, params)
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse) {
    if (error.error instanceof ErrorEvent) {
      // Client error
      console.error('An error occurred:', error.error.message);
    } else {
      // Server error
      console.error(
        `Backend returned code ${error.status}`, error.error);
    }
    
    return throwError('There was an error contacting the library service');
  };

  saveLibrary(libraryName: string): any {
    const body = {
        commitLibraries: [
            {
                libraryName: libraryName,
                unload: false
            }
        ]
    };

    return this.httpClient.post(`${environment.mediaServerUrlBase}/libraries/commit`, body);
  }
}

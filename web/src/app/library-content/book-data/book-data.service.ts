import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Subject, of } from 'rxjs';
import { map, shareReplay, timeout, catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BookDataService {
  constructor(private client: HttpClient) { }

  private bookSubjects = new Map<string, Subject<any>>();

  doGetBooks(libraryName: string) {
    if (!this.bookSubjects[libraryName]) {
      this.bookSubjects[libraryName] = new Subject();
    }

    const options = { params: new HttpParams().set('name', libraryName) };

    this.client.get(`${environment.mediaServerUrlBase}/books`, options).pipe(
      map(books => this.bookSubjects[libraryName].next(books)),
      shareReplay(1)
    ).subscribe();
  }

  getBooks(libraryName: string): Observable<any> {
    return this.bookSubjects[libraryName].asObservable();
  }

  updateBook(id: string, updateRequest: any, libraryName: string): any {
    return this.client.patch(`${environment.mediaServerUrlBase}/books/${id}?libraryName=${libraryName}`, updateRequest);
  }

  deleteBook(id: string, libraryName: string): Observable<any> {
    const options = { params: new HttpParams().set('name', libraryName) };

    return this.client.delete(`${environment.mediaServerUrlBase}/books/${id}`, options);
  }

  uploadBooks(files: any, libraryName: string): any {
    const formData = new FormData();
    for (var i = 0; i < files.length; i++) {
      formData.append('files', files.item(i));
    }

    formData.append('name', libraryName);

    return this.client.post(`${environment.mediaServerUrlBase}/libraries/upload`, formData).pipe(
      timeout(environment.uploadTimeoutMillis),
      catchError(e => {
        console.log('Upload error', e);
        return of(null);
      })
    );
  }

  addBook(libraryName: string, url: string, type: string): any {
    const request = {
      name: libraryName,
      request: {
        url: url,
        type: type
      }
    };

    return this.client.post(`${environment.mediaServerUrlBase}/books`, request).pipe(
      timeout(environment.uploadTimeoutMillis),
      catchError(e => {
        console.log('Add book error', e);
        return of(null);
      })
    );
  }
}

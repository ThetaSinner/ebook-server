import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
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
      const body = {
        name: libraryName,
        request: updateRequest
      };
      
      return this.client.patch(`${environment.mediaServerUrlBase}/books/${id}`, body);
    }
    
    deleteBook(id: string, libraryName: string): Observable<any> {
      const options = { params: new HttpParams().set('name', libraryName) };
      
      return this.client.delete(`${environment.mediaServerUrlBase}/books/${id}`, options);
    }
    
    uploadBooks(files: any, libraryName: string): any {
      throw new Error("Method not implemented.");
    }
  }
  
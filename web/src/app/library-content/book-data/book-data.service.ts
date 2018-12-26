import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BookDataService {

  constructor(private client: HttpClient) { }

  getBooks(libraryName: string) {
    const options = { params: new HttpParams().set('name', libraryName) }

    return this.client.get(`${environment.mediaServerUrlBase}/books`, options)
  }
}

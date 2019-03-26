import {Injectable} from '@angular/core';
import {Observable, of, Subject} from "rxjs";
import {HttpClient, HttpParams} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {catchError, map, shareReplay, timeout} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class VideoDataService {

  constructor(private client: HttpClient) { }

  private videoSubjects = new Map<string, Subject<any>>();

  doGetVideos(libraryName: string) {
    if (!this.videoSubjects[libraryName]) {
      this.videoSubjects[libraryName] = new Subject();
    }

    const options = { params: new HttpParams().set('name', libraryName) };

    this.client.get(`${environment.mediaServerUrlBase}/videos`, options).pipe(
      map(videos => this.videoSubjects[libraryName].next(videos)),
      shareReplay(1)
    ).subscribe();
  }

  getVideos(libraryName: string): Observable<any> {
    return this.videoSubjects[libraryName].asObservable();
  }

  uploadVideos(files: FileList, libraryName: string): Observable<any> {
    const formData = new FormData();
    for (let i = 0; i < files.length; i++) {
      formData.append('files', files.item(i));
    }

    formData.append('name', libraryName);

    return this.client.post(`${environment.mediaServerUrlBase}/videos/upload`, formData).pipe(
      timeout(environment.uploadTimeoutMillis),
      catchError(e => {
        console.log('Upload error', e);
        return of(null);
      })
    );
  }

  static getPlaybackUrl(libraryName: string, videoId: string): string {
    return `${environment.mediaServerUrlBase}/videos/${videoId}?name=${libraryName}`;
  }

  deleteBook(id: string, libraryName: string) {
    const options = { params: new HttpParams().set('name', libraryName) };

    return this.client.delete(`${environment.mediaServerUrlBase}/videos/${id}`, options);
  }
}

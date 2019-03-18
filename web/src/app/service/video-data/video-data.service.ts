import { Injectable } from '@angular/core';
import {Observable, of} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {catchError, timeout} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class VideoDataService {

  constructor(private client: HttpClient) { }

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
}

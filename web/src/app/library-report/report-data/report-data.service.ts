import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReportDataService {

  constructor(private client: HttpClient) { }

  getReportData(libraryName: string) {
    const options = { params: new HttpParams().set('libraryName', libraryName) };

    return this.client.get(`${environment.mediaServerUrlBase}/maintenance/report`, options)
  }

  applyFieldFix(libraryName: string, reportId: any, fieldFix: any) {
    const options = { params: new HttpParams().set('libraryName', libraryName) };

    return this.client.put(`${environment.mediaServerUrlBase}/maintenance/report/${reportId}`, fieldFix, options);
  }
}

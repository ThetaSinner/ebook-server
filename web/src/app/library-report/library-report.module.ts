import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LibraryReportWorkspaceComponent } from './library-report-workspace/library-report-workspace.component';
import { LibraryReportRoutingModule } from './library-report-routing.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ReportDataService } from './report-data/report-data.service';

@NgModule({
  declarations: [LibraryReportWorkspaceComponent],
  imports: [
    CommonModule,
    FontAwesomeModule,
    LibraryReportRoutingModule
  ],
  providers: [
    ReportDataService
  ]
})
export class LibraryReportModule { }

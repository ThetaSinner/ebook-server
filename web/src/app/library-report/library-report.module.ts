import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LibraryReportWorkspaceComponent } from './library-report-workspace/library-report-workspace.component';
import { LibraryReportRoutingModule } from './library-report-routing.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ReportDataService } from './report-data/report-data.service';
import { ContentBreakdownChartComponent } from './content-breakdown-chart/content-breakdown-chart.component';

@NgModule({
  declarations: [LibraryReportWorkspaceComponent, ContentBreakdownChartComponent],
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

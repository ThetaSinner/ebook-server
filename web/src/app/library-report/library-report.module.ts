import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LibraryReportWorkspaceComponent } from './library-report-workspace/library-report-workspace.component';
import { LibraryReportRoutingModule } from './library-report-routing.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ReportDataService } from './report-data/report-data.service';
import { ContentBreakdownChartComponent } from './content-breakdown-chart/content-breakdown-chart.component';
import { CurationCompletionChartComponent } from './curation-completion-chart/curation-completion-chart.component';
import { WebLinkFixComponent } from './report-fixing/web-link-fix/web-link-fix.component';
import { FixingWorkspaceComponent } from './report-fixing/fixing-workspace/fixing-workspace.component';
import { ReactiveFormsModule } from '@angular/forms';
import { LocalBookFixComponent } from './report-fixing/local-book-fix/local-book-fix.component';
import { MissingBookFixComponent } from './report-fixing/missing-book-fix/missing-book-fix.component';
import { CurationMetricsFixComponent } from './report-fixing/curation-metrics-fix/curation-metrics-fix.component';
import { UnlistedBookFixComponent } from './report-fixing/unlisted-book-fix/unlisted-book-fix.component';

@NgModule({
  declarations: [LibraryReportWorkspaceComponent, ContentBreakdownChartComponent, CurationCompletionChartComponent, WebLinkFixComponent, FixingWorkspaceComponent, LocalBookFixComponent, MissingBookFixComponent, CurationMetricsFixComponent, UnlistedBookFixComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FontAwesomeModule,
    LibraryReportRoutingModule
  ],
  providers: [
    ReportDataService
  ]
})
export class LibraryReportModule { }

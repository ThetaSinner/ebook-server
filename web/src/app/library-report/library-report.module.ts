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
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

@NgModule({
  declarations: [LibraryReportWorkspaceComponent, ContentBreakdownChartComponent, CurationCompletionChartComponent, WebLinkFixComponent, FixingWorkspaceComponent],
  imports: [
    CommonModule,
    FormsModule, // WTF angular
    ReactiveFormsModule,
    FontAwesomeModule,
    LibraryReportRoutingModule
  ],
  providers: [
    ReportDataService
  ]
})
export class LibraryReportModule { }

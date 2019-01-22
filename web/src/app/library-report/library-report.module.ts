import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LibraryReportWorkspaceComponent } from './library-report-workspace/library-report-workspace.component';
import { LibraryReportRoutingModule } from './library-report-routing.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@NgModule({
  declarations: [LibraryReportWorkspaceComponent],
  imports: [
    CommonModule,
    FontAwesomeModule,
    LibraryReportRoutingModule
  ]
})
export class LibraryReportModule { }

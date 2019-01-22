import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LibraryReportWorkspaceComponent } from './library-report-workspace/library-report-workspace.component';

const routes: Routes = [
  { 
    path: 'content/:libraryName/report',
    component: LibraryReportWorkspaceComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class LibraryReportRoutingModule { }

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LibraryContentWorkspaceComponent } from './library-content-workspace/library-content-workspace.component';

const routes: Routes = [
  { path: 'content/:libraryName', component: LibraryContentWorkspaceComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class LibraryContentRoutingModule { }

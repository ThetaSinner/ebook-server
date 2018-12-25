import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LibraryWorkspaceComponent } from './library-workspace/library-workspace.component';

const routes: Routes = [
  { path: 'libraries', component: LibraryWorkspaceComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class LibraryRoutingModule { }

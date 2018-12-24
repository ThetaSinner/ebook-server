import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LibrarySelectComponent } from './library-select/library-select.component';

const routes: Routes = [
  { path: 'libraries', component: LibrarySelectComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class LibraryRoutingModule { }

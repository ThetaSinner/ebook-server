import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LibraryAddWorkspaceComponent } from './library-add-workspace/library-add-workspace.component';
import { BookAddComponent } from './book-add/book-add.component';
import { BookUploadComponent } from './book-upload/book-upload.component';

const routes: Routes = [
  { 
    path: 'content/:libraryName/add',
    component: LibraryAddWorkspaceComponent,
    children: [{
        path: '',
        redirectTo: 'remote',
        pathMatch: 'full'
    }, {
        path: 'remote',
        component: BookAddComponent
    }, {
        path: 'upload',
        component: BookUploadComponent
    }]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class LibraryAddRoutingModule { }

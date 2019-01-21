import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LibraryAddWorkspaceComponent } from './library-add-workspace/library-add-workspace.component';
import { BookUploadComponent } from './book-upload/book-upload.component';
import { BookRemoteAddComponent } from './book-remote-add/book-remote-add.component';
import { BookLinkAddComponent } from './book-link-add/book-link-add.component';

const routes: Routes = [
  { 
    path: 'content/:libraryName/add',
    component: LibraryAddWorkspaceComponent,
    children: [{
        path: '',
        redirectTo: 'upload',
        pathMatch: 'full'
    }, {
        path: 'remote',
        component: BookRemoteAddComponent
    }, {
        path: 'upload',
        component: BookUploadComponent
    }, {
        path: 'link',
        component: BookLinkAddComponent
    }]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class LibraryAddRoutingModule { }

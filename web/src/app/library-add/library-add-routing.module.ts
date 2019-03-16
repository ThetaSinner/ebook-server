import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LibraryAddWorkspaceComponent} from './library-add-workspace/library-add-workspace.component';
import {BookUploadComponent} from './book-upload/book-upload.component';
import {BookRemoteAddComponent} from './book-remote-add/book-remote-add.component';
import {BookLinkAddComponent} from './book-link-add/book-link-add.component';
import {VideoUploadComponent} from "./video-upload/video-upload.component";
import {VideoRemoteAddComponent} from "./video-remote-add/video-remote-add.component";
import {VideoLinkAddComponent} from "./video-link-add/video-link-add.component";

const routes: Routes = [
  { 
    path: 'content/:libraryName/add',
    component: LibraryAddWorkspaceComponent,
    children: [{
        path: '',
        redirectTo: 'book/upload',
        pathMatch: 'full'
    }, {
        path: 'book/remote',
        component: BookRemoteAddComponent
    }, {
        path: 'book/upload',
        component: BookUploadComponent
    }, {
        path: 'book/link',
        component: BookLinkAddComponent
    }, {
      path: 'video/remote',
      component: VideoRemoteAddComponent
    }, {
      path: 'video/upload',
      component: VideoUploadComponent
    }, {
      path: 'video/link',
      component: VideoLinkAddComponent
    }]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class LibraryAddRoutingModule { }

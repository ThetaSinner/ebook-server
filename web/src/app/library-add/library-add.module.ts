import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {LibraryAddRoutingModule} from './library-add-routing.module';
import {LibraryAddWorkspaceComponent} from './library-add-workspace/library-add-workspace.component';
import {BookUploadComponent} from './book-upload/book-upload.component';
import {BookRemoteAddComponent} from './book-remote-add/book-remote-add.component';
import {BookLinkAddComponent} from './book-link-add/book-link-add.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {VideoUploadComponent} from './video-upload/video-upload/video-upload.component';

@NgModule({
  declarations: [
    LibraryAddWorkspaceComponent, 
    BookUploadComponent,
    BookRemoteAddComponent, 
    BookLinkAddComponent, VideoUploadComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FontAwesomeModule,
    LibraryAddRoutingModule,
    FormsModule
  ]
})
export class LibraryAddModule { }

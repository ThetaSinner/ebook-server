import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { BookAddComponent } from './book-add/book-add.component';
import { LibraryAddRoutingModule } from './library-add-routing.module';
import { LibraryAddWorkspaceComponent } from './library-add-workspace/library-add-workspace.component';
import { BookUploadComponent } from './book-upload/book-upload.component';

@NgModule({
  declarations: [LibraryAddWorkspaceComponent, BookAddComponent, BookUploadComponent],
  imports: [
    CommonModule,
    FontAwesomeModule,
    LibraryAddRoutingModule
  ]
})
export class LibraryAddModule { }

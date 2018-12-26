import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LibraryContentWorkspaceComponent } from './library-content-workspace/library-content-workspace.component';
import { BookDataService } from './book-data/book-data.service';
import { HttpClientModule } from '@angular/common/http';
import { LibraryContentRoutingModule } from './library-content-routing.module';

@NgModule({
  declarations: [LibraryContentWorkspaceComponent],
  imports: [
    CommonModule,
    HttpClientModule,
    LibraryContentRoutingModule
  ],
  providers: [
    BookDataService
  ]
})
export class LibraryContentModule { }

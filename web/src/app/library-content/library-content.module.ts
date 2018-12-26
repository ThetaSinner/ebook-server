import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LibraryContentWorkspaceComponent } from './library-content-workspace/library-content-workspace.component';
import { BookDataService } from './book-data/book-data.service';
import { HttpClientModule } from '@angular/common/http';
import { LibraryContentRoutingModule } from './library-content-routing.module';
import { ControlBarComponent } from './control-bar/control-bar.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ContentTableComponent } from './content-table/content-table.component';
import { ValueListPipe } from './value-list/value-list.pipe';

@NgModule({
  declarations: [LibraryContentWorkspaceComponent, ControlBarComponent, ContentTableComponent, ValueListPipe],
  imports: [
    CommonModule,
    HttpClientModule,
    FontAwesomeModule,
    LibraryContentRoutingModule
  ],
  providers: [
    BookDataService
  ]
})
export class LibraryContentModule { }

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LibrarySelectComponent } from './library-select/library-select.component';
import { LibraryRoutingModule } from './library-routing.module';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@NgModule({
  declarations: [LibrarySelectComponent],
  imports: [
    CommonModule,
    FontAwesomeModule,
    LibraryRoutingModule
  ]
})
export class LibraryModule { }

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LibrarySelectComponent } from './library-select/library-select.component';
import { LibraryRoutingModule } from './library-routing.module';

@NgModule({
  declarations: [LibrarySelectComponent],
  imports: [
    CommonModule,
    LibraryRoutingModule
  ]
})
export class LibraryModule { }

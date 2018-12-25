import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { LibraryCreateComponent } from './library-create/library-create.component';
import { LibraryDataService } from './library-data/library-data.service';
import { LibraryRoutingModule } from './library-routing.module';
import { LibrarySelectComponent } from './library-select/library-select.component';
import { LibraryWorkspaceComponent } from './library-workspace/library-workspace.component';

@NgModule({
  declarations: [LibrarySelectComponent, LibraryCreateComponent, LibraryWorkspaceComponent],
  imports: [
    CommonModule,
    HttpClientModule,
    FontAwesomeModule,
    LibraryRoutingModule
  ],
  providers: [
    LibraryDataService
  ]
})
export class LibraryModule { }

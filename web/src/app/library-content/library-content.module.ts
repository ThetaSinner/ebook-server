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
import { ContentDetailComponent } from './content-detail/content-detail.component';
import { InfoHostDirective } from './control-bar/info-host/info-host.directive';
import { LibraryInfoComponent } from './control-bar/library-info/library-info.component';
import { InfoHostDataSourceService } from './control-bar/info-host-data-source/info-host-data-source.service';
import { ChangeInfoComponent } from './control-bar/change-info/change-info.component';
import { ContentEditComponent } from './content-edit/content-edit.component';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    LibraryContentWorkspaceComponent,
    ControlBarComponent,
    ContentTableComponent,
    ValueListPipe,
    ContentDetailComponent, 
    InfoHostDirective,
    LibraryInfoComponent,
    ChangeInfoComponent,
    ContentEditComponent 
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    ReactiveFormsModule,
    FontAwesomeModule,
    LibraryContentRoutingModule
  ],
  providers: [
    BookDataService,
    InfoHostDataSourceService
  ],
  entryComponents: [
    LibraryInfoComponent,
    ChangeInfoComponent
  ]
})
export class LibraryContentModule { }

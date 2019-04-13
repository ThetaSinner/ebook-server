import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {NgbDatepickerModule} from '@ng-bootstrap/ng-bootstrap';
import {ContentDetailComponent} from './content-detail/content-detail.component';
import {ContentEditComponent} from './content-edit/content-edit.component';
import {BookContentTableComponent} from './book-content-table/book-content-table.component';
import {ChangeInfoComponent} from './control-bar/change-info/change-info.component';
import {ControlBarComponent} from './control-bar/control-bar.component';
import {InfoHostDataSourceService} from './control-bar/info-host-data-source/info-host-data-source.service';
import {InfoHostDirective} from './control-bar/info-host/info-host.directive';
import {LibraryInfoComponent} from './control-bar/library-info/library-info.component';
import {LibraryContentRoutingModule} from './library-content-routing.module';
import {LibraryContentWorkspaceComponent} from './library-content-workspace/library-content-workspace.component';
import {ValueListPipe} from './value-list/value-list.pipe';
import {VideoSummaryTableComponent} from './video-summary-table/video-summary-table.component';
import {VideoContentDetailComponent} from './video-content-detail/video-content-detail.component';
import { VideoContentEditComponent } from './video-content-edit/video-content-edit.component';

@NgModule({
  declarations: [
    LibraryContentWorkspaceComponent,
    ControlBarComponent,
    BookContentTableComponent,
    ValueListPipe,
    ContentDetailComponent, 
    InfoHostDirective,
    LibraryInfoComponent,
    ChangeInfoComponent,
    ContentEditComponent,
    VideoSummaryTableComponent,
    VideoContentDetailComponent,
    VideoContentEditComponent
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    ReactiveFormsModule,
    NgbDatepickerModule,
    FontAwesomeModule,
    LibraryContentRoutingModule
  ],
  providers: [
    InfoHostDataSourceService
  ],
  entryComponents: [
    LibraryInfoComponent,
    ChangeInfoComponent
  ]
})
export class LibraryContentModule { }

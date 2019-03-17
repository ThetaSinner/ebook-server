import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LibraryAddModule } from './library-add/library-add.module';
import { LibraryContentModule } from './library-content/library-content.module';
import { LibraryModule } from './library/library.module';
import { LibraryReportModule } from './library-report/library-report.module';
import {VideoDataService} from "./service/video-data/video-data.service";
import { PlaybackComponent } from './video/playback/playback.component';

@NgModule({
  declarations: [
    AppComponent,
    PlaybackComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    LibraryModule,
    LibraryContentModule,
    LibraryAddModule,
    LibraryReportModule
  ],
  providers: [VideoDataService],
  bootstrap: [AppComponent]
})
export class AppModule { }

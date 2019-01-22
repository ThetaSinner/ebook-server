import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LibraryAddModule } from './library-add/library-add.module';
import { LibraryContentModule } from './library-content/library-content.module';
import { LibraryModule } from './library/library.module';
import { LibraryReportModule } from './library-report/library-report.module';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    LibraryModule,
    LibraryContentModule,
    LibraryAddModule,
    LibraryReportModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { LibraryModule } from './library/library.module';
import { LibraryContentModule } from './library-content/library-content.module';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    LibraryModule,
    LibraryContentModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

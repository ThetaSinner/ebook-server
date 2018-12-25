import { Component, OnInit } from '@angular/core';
import { LibraryDataService } from '../library-data/library-data.service';

@Component({
  selector: 'app-library-create',
  templateUrl: './library-create.component.html',
  styleUrls: ['./library-create.component.scss']
})
export class LibraryCreateComponent {

  constructor(private libraryDataService: LibraryDataService) { }

  createLibrary(libraryName: string) {
    const subscription = this.libraryDataService.createLibrary(libraryName).subscribe(_ => {
      subscription.unsubscribe();
    });
  }
}

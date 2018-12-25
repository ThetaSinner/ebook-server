import { Component, OnInit } from '@angular/core';
import { LibraryDataService } from '../library-data/library-data.service';

@Component({
  selector: 'app-library-workspace',
  templateUrl: './library-workspace.component.html',
  styleUrls: ['./library-workspace.component.scss']
})
export class LibraryWorkspaceComponent implements OnInit {

  constructor(private libraryDataService: LibraryDataService) { }

  ngOnInit() {
  }

  createLibrary(libraryName: string) {
    const subscription = this.libraryDataService.createLibrary(libraryName).subscribe(_ => {
      subscription.unsubscribe();
    });
  }
}

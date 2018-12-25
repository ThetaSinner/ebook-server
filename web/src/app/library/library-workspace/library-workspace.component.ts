import { Component, OnInit } from '@angular/core';
import { LibraryDataService } from '../library-data/library-data.service';

@Component({
  selector: 'app-library-workspace',
  templateUrl: './library-workspace.component.html',
  styleUrls: ['./library-workspace.component.scss']
})
export class LibraryWorkspaceComponent implements OnInit {
  libraryData: any;

  constructor(private libraryDataService: LibraryDataService) { }

  ngOnInit() {
    this.getLibraries();
  }

  createLibrary(libraryName: string) {
    const component = this;
    const subscription = this.libraryDataService.createLibrary(libraryName).subscribe(_ => {
      component.getLibraries();
      subscription.unsubscribe();
    });
  }

  private getLibraries() {
    const component = this;
    const subscription = this.libraryDataService.getLibraries().subscribe(libraryData => {
      component.libraryData = libraryData;
      subscription.unsubscribe();
    });
  }
}

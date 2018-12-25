import { Component, OnInit } from '@angular/core';
import { faFolderOpen, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { LibraryDataService } from '../library-data/library-data.service';

@Component({
  selector: 'app-library-select',
  templateUrl: './library-select.component.html',
  styleUrls: ['./library-select.component.scss']
})
export class LibrarySelectComponent implements OnInit {
  faFolderOpen: IconDefinition = faFolderOpen;

  libraryData: any;

  constructor(private libraryDataService: LibraryDataService) { }

  ngOnInit() {
    const component = this;
    const subscription = this.libraryDataService.getLibraries().subscribe(libraryData => {
      component.libraryData = libraryData;
      subscription.unsubscribe();
    });
  }
}

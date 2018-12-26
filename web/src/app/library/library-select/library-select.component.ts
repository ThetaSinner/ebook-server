import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { faFolderOpen, IconDefinition } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-library-select',
  templateUrl: './library-select.component.html',
  styleUrls: ['./library-select.component.scss']
})
export class LibrarySelectComponent {
  faFolderOpen: IconDefinition = faFolderOpen;

  @Output() select = new EventEmitter<string>();

  @Input() libraryData: any;

  constructor() { }

  selectLibrary(libraryName: string) {
    this.select.emit(libraryName);
  }
}

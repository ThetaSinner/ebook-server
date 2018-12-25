import { Component, Input, OnInit } from '@angular/core';
import { faFolderOpen, IconDefinition } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-library-select',
  templateUrl: './library-select.component.html',
  styleUrls: ['./library-select.component.scss']
})
export class LibrarySelectComponent {
  faFolderOpen: IconDefinition = faFolderOpen;

  @Input() libraryData: any;

  constructor() { }
}

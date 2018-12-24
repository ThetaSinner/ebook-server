import { Component, OnInit } from '@angular/core';
import { faFolderOpen, IconDefinition } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-library-select',
  templateUrl: './library-select.component.html',
  styleUrls: ['./library-select.component.scss']
})
export class LibrarySelectComponent implements OnInit {
  faFolderOpen: IconDefinition = faFolderOpen;

  constructor() { }

  ngOnInit() {
  }

}

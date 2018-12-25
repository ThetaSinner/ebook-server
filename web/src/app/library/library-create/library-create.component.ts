import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-library-create',
  templateUrl: './library-create.component.html',
  styleUrls: ['./library-create.component.scss']
})
export class LibraryCreateComponent {
  @Output() create = new EventEmitter<string>();

  constructor() { }

  createLibrary(libraryName: string) {
    this.create.emit(libraryName);
  }
}

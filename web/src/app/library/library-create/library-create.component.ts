import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-library-create',
  templateUrl: './library-create.component.html',
  styleUrls: ['./library-create.component.scss']
})
export class LibraryCreateComponent implements OnInit {
  @Output() create = new EventEmitter<string>();
  
  createForm: FormGroup
  
  constructor(private formBuilder: FormBuilder) { }
  
  ngOnInit(): void {
    this.createForm = this.formBuilder.group({
      libraryName: ['']
    });
  }

  createLibrary(libraryName: string) {
    this.create.emit(libraryName);
    this.createForm.reset();
  }
}

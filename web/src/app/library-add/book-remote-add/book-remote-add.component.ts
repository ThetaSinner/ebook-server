import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';

@Component({
  selector: 'app-book-remote-add',
  templateUrl: './book-remote-add.component.html',
  styleUrls: ['./book-remote-add.component.scss']
})
export class BookRemoteAddComponent implements OnInit {
  addRemoteForm: FormGroup;

  constructor(private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.addRemoteForm = this.formBuilder.group({
      bookUrl: ['']
    });
  }

  handleAdd() {
    console.log('Book to add', this.addRemoteForm.value.bookUrl);
  }
}

import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl } from '@angular/forms';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { BookDataService } from 'src/app/library-content/book-data/book-data.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-book-remote-add',
  templateUrl: './book-remote-add.component.html',
  styleUrls: ['./book-remote-add.component.scss']
})
export class BookRemoteAddComponent implements OnInit {
  private libraryName: string;
  addRemoteForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private activeRoute: ActivatedRoute,
    private bookDataService: BookDataService
  ) { }

  ngOnInit() {
    this.addRemoteForm = this.formBuilder.group({
      bookUrl: ['']
    });

    this.activeRoute.parent.paramMap.pipe(
      map((params: ParamMap) => params.get('libraryName'))
    ).subscribe(libraryName => {
      this.libraryName = libraryName;
    });
  }

  handleAdd() {
    const url = this.addRemoteForm.value.bookUrl;
    const sub = this.bookDataService.addBook(this.libraryName, url, 'LocalUnmanaged').subscribe(() => {
      this.addRemoteForm.setControl('bookUrl', new FormControl(''))
      sub.unsubscribe();
    });
  }
}

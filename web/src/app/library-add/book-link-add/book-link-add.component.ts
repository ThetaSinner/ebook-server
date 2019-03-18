import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormBuilder } from '@angular/forms';
import { ParamMap, ActivatedRoute } from '@angular/router';
import { map } from 'rxjs/operators';
import { BookDataService } from 'src/app/service/book-data/book-data.service';

@Component({
  selector: 'app-book-link-add',
  templateUrl: './book-link-add.component.html',
  styleUrls: ['./book-link-add.component.scss']
})
export class BookLinkAddComponent implements OnInit {
  private libraryName: string;
  addLinkForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private activeRoute: ActivatedRoute,
    private bookDataService: BookDataService
  ) { }

  ngOnInit() {
    this.addLinkForm = this.formBuilder.group({
      bookUrl: ['']
    });

    this.activeRoute.parent.paramMap.pipe(
      map((params: ParamMap) => params.get('libraryName'))
    ).subscribe(libraryName => {
      this.libraryName = libraryName;
    });
  }

  handleAdd() {
    const url = this.addLinkForm.value.bookUrl;
    const sub = this.bookDataService.addBook(this.libraryName, url, 'WebLink').subscribe(() => {
      this.addLinkForm.setControl('bookUrl', new FormControl(''))
      sub.unsubscribe();
    });
  }
}

import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { IconDefinition, findIconDefinition } from '@fortawesome/fontawesome-svg-core';
import { faPlus, faMinus, faCalendarAlt, faCheck, faTimesCircle } from '@fortawesome/free-solid-svg-icons';
import { NgbCalendar, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { BookDataService } from '../book-data/book-data.service';
import { compare } from 'fast-json-patch';

@Component({
  selector: 'app-content-edit',
  templateUrl: './content-edit.component.html',
  styleUrls: ['./content-edit.component.scss']
})
export class ContentEditComponent implements OnInit {
  addIcon: IconDefinition = faPlus;
  removeIcon: IconDefinition = faMinus;
  calendarIcon: IconDefinition = faCalendarAlt;
  saveChangesIcon: IconDefinition = faCheck;
  cancelChangesIcon: IconDefinition = faTimesCircle;

  detailForm: FormGroup;
  @Input() detailData;
  @Input() libraryName: string;
  
  @Output() editFinished = new EventEmitter();

  constructor(
    private formBuilder: FormBuilder,
    private bookDataService: BookDataService,
    private calendar: NgbCalendar
  ) { }

  ngOnInit() {
    const metadata: any = {};
    if (this.detailData.metadata) {
      metadata.rating = this.detailData.metadata.rating;
      metadata.tags = this.detailData.metadata.tags;
    }

    let authors = [
      this.formBuilder.control('')
    ];
    if (this.detailData.authors && this.detailData.authors.length) {
      authors = this.detailData.authors.map((author: string) => 
        this.formBuilder.control(author)
      );
    }

    let tags = [
      this.formBuilder.control('')
    ];
    if (metadata.tags && metadata.tags.length) {
      tags = metadata.tags.map((tag: string) => 
        this.formBuilder.control(tag)
      );
    }

    const datePublished: NgbDate = this.calendar.getToday();
    if (this.detailData.datePublished) {
      const savedDatePublished = new Date(this.detailData.datePublished);

      datePublished.year = savedDatePublished.getFullYear();
      datePublished.month = savedDatePublished.getMonth() + 1;
      datePublished.day = savedDatePublished.getDate();
    }

    this.detailForm = this.formBuilder.group({
      title: [this.detailData.title ? this.detailData.title : ''],
      // See https://www.oreilly.com/library/view/regular-expressions-cookbook/9781449327453/ch04s13.html
      isbn: [this.detailData.isbn ? this.detailData.isbn : '', Validators.pattern('^(?:ISBN(?:-1[03])?:?●)?(?=[0-9X]{10}$|(?=(?:[0-9]+[-●]){3})[-●0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[-●]){4})[-●0-9]{17}$)(?:97[89][-●]?)?[0-9]{1,5}[-●]?[0-9]+[-●]?[0-9]+[-●]?[0-9X]$')],
      publisher: [this.detailData.publisher ? this.detailData.publisher : ''],
      description: [this.detailData.publisher ? this.detailData.publisher : ''],
      rating: [metadata.rating ? metadata.rating : ''],
      datePublished: [datePublished],
      authors: this.formBuilder.array(authors),
      tags: this.formBuilder.array(tags)
    });
  }

  get authorsControl() {
    return this.detailForm.get('authors') as FormArray;
  }

  addAuthorInput() {
    this.authorsControl.push(this.formBuilder.control(''));
  }

  removeAuthorInput(index: number) {
    this.authorsControl.removeAt(index);
  }

  get tagsControl() {
    return this.detailForm.get('tags') as FormArray;
  }

  addTagInput() {
    this.tagsControl.push(this.formBuilder.control(''));
  }

  removeTagInput(index: number) {
    this.tagsControl.removeAt(index);
  }

  saveChanges() {
    const formOutput = this.detailForm.getRawValue();

    // Create update request with the fields that can't be edited.
    const updatedBook: any = {
      id: this.detailData.id,
      url: this.detailData.url,
      covers: this.detailData.covers
    };

    if (formOutput.title) {
      updatedBook.title = formOutput.title;
    }
    if (formOutput.isbn) {
      updatedBook.isbn = formOutput.isbn;
    }
    if (formOutput.publisher) {
      updatedBook.publisher = formOutput.publisher;
    }
    if (formOutput.description) {
      updatedBook.description = formOutput.description;
    }
    if (formOutput.datePublished) {
      const dateStruct = formOutput.datePublished;
      updatedBook.datePublished = new Date(dateStruct.year, dateStruct.month - 1, dateStruct.day);
    }
    
    if (this.isArrayValid(formOutput, 'authors')) {
      updatedBook.authors = formOutput.authors.filter((author: any) => author);
    }

    if (formOutput.rating || this.isArrayValid(formOutput, 'tags')) {
      updatedBook.metadata = {};

      if (formOutput.rating) {
        updatedBook.metadata.rating = formOutput.rating;
      }

      if (this.isArrayValid(formOutput, 'tags')) {
        updatedBook.metadata.tags = formOutput.tags.filter(tag => tag);
      }
    }

    const diff = compare(this.detailData, updatedBook);

    const sub = this.bookDataService.updateBook(this.detailData.id, diff, this.libraryName).subscribe((updatedBook) => {
      this.finishEdit(updatedBook);
      sub.unsubscribe();
    });
  }

  private isArrayValid(objectToTest: object, field: string) {
    if (objectToTest[field]) {
      const array = objectToTest[field].filter((val: any) => val);
      
      return array.length
    }

    return false;
  }

  private finishEdit(updatedBook) {
    this.editFinished.emit(updatedBook);
  }

  cancelEdit() {
    this.editFinished.emit(null);
  }

  get isbn() { 
    return this.detailForm.get('isbn'); 
  }
}

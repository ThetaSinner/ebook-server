import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, FormArray } from '@angular/forms';
import { IconDefinition, findIconDefinition } from '@fortawesome/fontawesome-svg-core';
import { faPlus, faMinus, faCalendarAlt, faCheck, faTimesCircle } from '@fortawesome/free-solid-svg-icons';
import { NgbCalendar, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { BookDataService } from '../book-data/book-data.service';

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
      isbn: [this.detailData.isbn ? this.detailData.isbn : ''],
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

    const updateRequest: any = {};
    if (formOutput.title) {
      updateRequest.title = formOutput.title;
    }
    if (formOutput.isbn) {
      updateRequest.isbn = formOutput.isbn;
    }
    if (formOutput.publisher) {
      updateRequest.publisher = formOutput.publisher;
    }
    if (formOutput.description) {
      updateRequest.description = formOutput.description;
    }
    if (formOutput.datePublished) {
      const dateStruct = formOutput.datePublished;
      updateRequest.datePublished = new Date(dateStruct.year, dateStruct.month - 1, dateStruct.day);
    }
    if (formOutput.authors) {
      updateRequest.authors = formOutput.authors;
    }

    if (formOutput.rating || formOutput.tags) {
      updateRequest.metadata = {};

      if (formOutput.rating) {
        updateRequest.metadata.rating = formOutput.rating;
      }
      if (formOutput.tags) {
        updateRequest.metadata.tags = formOutput.tags;
      }
    }

    const sub = this.bookDataService.updateBook(this.detailData.id, updateRequest, this.libraryName).subscribe((updatedBook) => {
      this.finishEdit(updatedBook);
      sub.unsubscribe();
    });
  }

  private finishEdit(updatedBook) {
    this.editFinished.emit(updatedBook);
  }

  cancelEdit() {
    this.editFinished.emit(null);
  }
}

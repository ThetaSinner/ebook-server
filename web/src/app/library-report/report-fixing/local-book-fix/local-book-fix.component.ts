import { Component, OnInit, SimpleChanges, Input } from '@angular/core';
import { FormBuilder, FormGroup, FormArray } from '@angular/forms';
import { BookDataService } from 'src/app/service/book-data/book-data.service';

@Component({
  selector: 'app-local-book-fix',
  templateUrl: './local-book-fix.component.html',
  styleUrls: ['./local-book-fix.component.scss']
})
export class LocalBookFixComponent implements OnInit {
  @Input() fixLocalBooks: any;
  @Input() libraryName: string;
  updateInputsForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private bookService: BookDataService
  ) {
    this.updateInputsForm = formBuilder.group({
      updateInputs: formBuilder.array([])
    });
  }

  ngOnInit() {
    this.buildForm();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.fixLocalBooks.currentValue) {
      this.fixLocalBooks = changes.fixLocalBooks.currentValue;
      this.buildForm();
    }
  }

  buildForm() {
    if (!this.fixLocalBooks) {
      return;
    }

    const arr = new Array(this.fixLocalBooks.length);
    arr.fill(this.formBuilder.control(''));

    this.updateInputsForm = this.formBuilder.group({
      updateInputs: this.formBuilder.array(arr)
    });
  }

  get updateInputs(): FormArray {
    return this.updateInputsForm.get('updateInputs') as FormArray;
  }

  getFixLocalBook(index: number) {
    return this.fixLocalBooks[index];
  }

  updateBrokenLocalBook(index: number) {
    const sub = this.bookService.updateBook(this.fixLocalBooks[index].bookId, [{
      op: "replace",
      path: "/url/value",
      value: this.updateInputs.at(index).value
    }], this.libraryName).subscribe(() => {
      sub.unsubscribe();
    });
  }
}

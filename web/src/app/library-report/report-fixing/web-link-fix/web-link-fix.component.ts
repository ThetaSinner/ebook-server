import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { FormBuilder, FormArray, FormGroup } from '@angular/forms';
import { BookDataService } from 'src/app/library-content/book-data/book-data.service';

@Component({
  selector: 'app-web-link-fix',
  templateUrl: './web-link-fix.component.html',
  styleUrls: ['./web-link-fix.component.scss']
})
export class WebLinkFixComponent implements OnInit, OnChanges {
  @Input() fixWebLinks: any;
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
    if (changes.fixWebLinks.currentValue) {
      this.fixWebLinks = changes.fixWebLinks.currentValue;
      this.buildForm();
    }
  }

  buildForm() {
    if (!this.fixWebLinks) {
      return;
    }

    const arr = new Array(this.fixWebLinks.length);
    arr.fill(this.formBuilder.control(''));

    this.updateInputsForm = this.formBuilder.group({
      updateInputs: this.formBuilder.array(arr)
    });
  }

  get updateInputs(): FormArray {
    return this.updateInputsForm.get('updateInputs') as FormArray;
  }

  getFixWebLink(index: number) {
    return this.fixWebLinks[index];
  }

  updateBrokenWebLink(index: number) {
    const sub = this.bookService.updateBook(this.fixWebLinks[index].bookId, [{
      op: "replace",
      path: "/url/value",
      value: this.updateInputs.at(index).value
    }], this.libraryName).subscribe(() => {
      sub.unsubscribe();
    });
  }
}

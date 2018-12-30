import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup, FormArray } from '@angular/forms';
import { IconDefinition } from '@fortawesome/fontawesome-svg-core';
import { faPlus, faMinus } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-content-edit',
  templateUrl: './content-edit.component.html',
  styleUrls: ['./content-edit.component.scss']
})
export class ContentEditComponent implements OnInit {
  addIcon: IconDefinition = faPlus;
  removeIcon: IconDefinition = faMinus;

  detailForm: FormGroup;
  @Input() detailData;
  
  constructor(private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.detailForm = this.formBuilder.group({
      title: [''],
      isbn: [''],
      publisher: [''],
      description: [''],
      rating: [''],
      authors: this.formBuilder.array([
        this.formBuilder.control('')
      ]),
      tags: this.formBuilder.array([
        this.formBuilder.control('')
      ])
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
}

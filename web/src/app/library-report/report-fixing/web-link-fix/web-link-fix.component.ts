import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { FormBuilder, FormArray, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-web-link-fix',
  templateUrl: './web-link-fix.component.html',
  styleUrls: ['./web-link-fix.component.scss']
})
export class WebLinkFixComponent implements OnInit, OnChanges {
  @Input() fixWebLinks: any;
  updateInputsForm: FormGroup;

  constructor(private formBuilder: FormBuilder) { }

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
    console.log(index);
    return this.fixWebLinks[index];
  }

  run() {

  }
}

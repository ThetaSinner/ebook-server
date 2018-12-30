import { Component, OnInit, Input } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-content-edit',
  templateUrl: './content-edit.component.html',
  styleUrls: ['./content-edit.component.scss']
})
export class ContentEditComponent implements OnInit {
  detailForm: FormGroup;
  @Input() detailData;
  
  constructor(private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.detailForm = this.formBuilder.group({
      title: ['']
    });
  }

}

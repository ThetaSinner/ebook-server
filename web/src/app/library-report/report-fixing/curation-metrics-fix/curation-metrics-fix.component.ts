import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CurationDataFieldName } from '../../curation-completion-chart/curation-completion-chart.component';
import { FormGroup, FormBuilder, FormArray } from '@angular/forms';

@Component({
  selector: 'app-curation-metrics-fix',
  templateUrl: './curation-metrics-fix.component.html',
  styleUrls: ['./curation-metrics-fix.component.scss']
})
export class CurationMetricsFixComponent implements OnInit, OnChanges {
  @Input() curationMetrics: any;
  @Input() fixCurationFieldNames: any[] = [];
  @Input() libraryName: string;

  fixForm: FormGroup;

  constructor(private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.makeRenderModel();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.curationMetrics && changes.curationMetrics.currentValue) {
      this.curationMetrics = changes.curationMetrics.currentValue;
    }
    if (changes.fixCurationFieldNames && changes.fixCurationFieldNames.currentValue) {
      this.fixCurationFieldNames = changes.fixCurationFieldNames.currentValue;
    }

    this.makeRenderModel();
  }

  private makeRenderModel() {
    const model = {};

    if (this.fixCurationFieldNames.indexOf(CurationDataFieldName.Title) !== -1) {
      this.curationMetrics.booksWithMissingTitles.reduce(this.makeReducer('title'), model);
    }

    if (this.fixCurationFieldNames.indexOf(CurationDataFieldName.Publisher) !== -1) {
      this.curationMetrics.booksWithMissingPublisher.reduce(this.makeReducer('publisher'), model);
    }

    if (this.fixCurationFieldNames.indexOf(CurationDataFieldName.Authors) !== -1) {
      this.curationMetrics.booksWithMissingAuthors.reduce(this.makeReducer('authors'), model);
    }

    this.fixForm = this.formBuilder.group({
      fixItems: this.formBuilder.array(Object.keys(model).map(key => {
        const v = model[key];
        v.bookId = key;
        const group = {};
        if (v.title) {
          group['title'] = [''];
        }
        if (v.publisher) {
          group['publisher'] = [''];
        }
        if (v.authors) {
          group['authors'] = [''];
        }
        return this.formBuilder.group(group);
      }))
    });
  }

  get fixItems(): FormArray {
    return this.fixForm.get('fixItems') as FormArray;
  }

  updateCurationForModel(index: number) {
    const group = this.fixItems.at(index) as FormGroup;
    console.log(group.getRawValue());
  }

  private makeReducer(addkey: string) {
    return (result, value) => {
      const key = value.bookId;
      const fixModel = result[key] || {};
      fixModel[addkey] = true;
      fixModel.uriFragment = value.uriFragment;
      result[key] = fixModel;

      return result;
    };
  }
}

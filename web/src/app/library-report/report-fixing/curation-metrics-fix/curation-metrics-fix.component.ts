import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CurationDataFieldName } from '../../curation-completion-chart/curation-completion-chart.component';

@Component({
  selector: 'app-curation-metrics-fix',
  templateUrl: './curation-metrics-fix.component.html',
  styleUrls: ['./curation-metrics-fix.component.scss']
})
export class CurationMetricsFixComponent implements OnInit, OnChanges {
  @Input() curationMetrics: any;
  @Input() fixCurationFieldNames: any[];
  @Input() libraryName: string;
  renderModel: {};
  
  constructor() { }
  
  ngOnInit() {
    console.log('dingding!', this.curationMetrics, this.fixCurationFieldNames);
    this.makeRenderModel();
  }
  
  ngOnChanges(changes: SimpleChanges): void {
    console.log('changes', changes);

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
      console.log('adding titles');
      this.curationMetrics.booksWithMissingTitles.reduce(this.makeReducer('title'), model);
    }
    
    if (this.fixCurationFieldNames.indexOf(CurationDataFieldName.Publisher) !== -1) {
      console.log('adding publishers');
      this.curationMetrics.booksWithMissingPublisher.reduce(this.makeReducer('publisher'), model);
    }

    if (this.fixCurationFieldNames.indexOf(CurationDataFieldName.Authors) !== -1) {
      console.log('adding authors');
      this.curationMetrics.booksWithMissingAuthors.reduce(this.makeReducer('authors'), model);
    }

    this.renderModel = Object.keys(model).map(key => {
      const v = model[key];
      v.bookId = key;
      return v;
    });

    console.log(this.renderModel);
  }

  private makeReducer(addkey: string) {
    return (result, value) => {
      const key = value.bookId;
      const fixModel = result[key] || {};
      fixModel[addkey] = true;
      result[key] = fixModel;

      return result;
    };
  }
}

import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CurationDataFieldName } from '../../curation-completion-chart/curation-completion-chart.component';
import { FormGroup, FormBuilder, FormArray } from '@angular/forms';
import { BookDataService } from 'src/app/service/book-data/book-data.service';
import { compare } from 'fast-json-patch';
import { IconDefinition } from '@fortawesome/fontawesome-svg-core';
import { faPlus, faMinus } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-curation-metrics-fix',
  templateUrl: './curation-metrics-fix.component.html',
  styleUrls: ['./curation-metrics-fix.component.scss']
})
export class CurationMetricsFixComponent implements OnInit, OnChanges {
  @Input() curationMetrics: any;
  @Input() fixCurationFieldNames: any[] = [];
  @Input() libraryName: string;

  faPlusIcon: IconDefinition = faPlus;
  faMinusIcon: IconDefinition = faMinus;

  renderModel: any[];
  fixForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private bookService: BookDataService
  ) { }

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

    this.renderModel = Object.keys(model).map(key => {
      const v = model[key];
      v.bookId = key;
      return v;
    });

    const fixItems = this.renderModel.map(model => {
      const group = {};
      if (model.title) {
        group['title'] = [''];
      }
      if (model.publisher) {
        group['publisher'] = [''];
      }
      if (model.authors) {
        group['authors'] = this.formBuilder.array([
          this.formBuilder.control('')
        ]);
      }
      return this.formBuilder.group(group);
    })

    this.fixForm = this.formBuilder.group({
      fixItems: this.formBuilder.array(fixItems)
    });
  }

  get fixItems(): FormArray {
    return this.fixForm.get('fixItems') as FormArray;
  }

  addAuthorControl(fixItem: FormGroup) {
    const authors = fixItem.get('authors') as FormArray;
    authors.push(this.formBuilder.control(''));
  }

  removeAuthorControl(fixItem: FormGroup, index: number) {
    const authors = fixItem.get('authors') as FormArray;
    authors.removeAt(index);
  }

  updateCurationForModel(index: number) {
    const group = this.fixItems.at(index) as FormGroup;
    const formData = group.getRawValue();

    const updatedBook = {
      title: formData.title,
      publisher: formData.publisher,
      authors: formData.authors
    };

    const diff = compare({}, updatedBook);

    const sub = this.bookService.updateBook(this.renderModel[index].bookId, diff, this.libraryName)
      .subscribe(() => {
        sub.unsubscribe();
      });
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

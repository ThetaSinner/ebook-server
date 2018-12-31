import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { faChevronDown, faChevronUp, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-content-table',
  templateUrl: './content-table.component.html',
  styleUrls: ['./content-table.component.scss']
})
export class ContentTableComponent implements OnInit {
  showDetails: object = {};
  editDetails: object = {};

  @Input() libraryData$;
  libraryName: string;
  tableData$: Observable<any>;

  @Output() contentChanged = new EventEmitter();

  constructor() { }

  ngOnInit() {
    this.libraryData$.subscribe(libraryData => {
      this.libraryName = libraryData.libraryName;
      this.tableData$ = libraryData.books$;
    });
  }

  getTags(book: any): string[] {
    if (book && book.metadata) {
      return book.metadata.tags;
    }

    return [];
  }

  getRating(book: any) {
    if (book && book.metadata) {
      return book.metadata.rating;
    }

    return '';
  }

  toggleDetails(rowId: string) {
    this.showDetails[rowId] = !this.showDetails[rowId];
  }

  isShowDetails(rowId: string): boolean {
    return this.showDetails[rowId];
  }

  getIcon(rowId: string): IconDefinition {
    if (this.showDetails[rowId]) {
      return faChevronUp;
    }

    return faChevronDown;
  }

  editDetail(rowId: string) {
    this.editDetail[rowId] = !this.editDetail[rowId];
  }

  isEditDetails(rowId: string): boolean {
    return this.editDetail[rowId];
  }

  finishEdit(rowId: string) {
    this.editDetail[rowId] = false;
  }

  handleContentChanged() {
    this.contentChanged.next();
  }
}

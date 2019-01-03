import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { faChevronDown, faChevronUp, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { InfoHostDataSourceService } from '../control-bar/info-host-data-source/info-host-data-source.service';
import { InfoHostItem } from '../control-bar/info-host/info-host-item';
import { LibraryInfoComponent } from '../control-bar/library-info/library-info.component';

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

  constructor(
    private infoHostDataSourceService: InfoHostDataSourceService
  ) { }

  ngOnInit() {
    this.libraryData$.subscribe(libraryData => {
      this.libraryName = libraryData.libraryName;
      this.tableData$ = libraryData.books$;

      this.tableData$.pipe(
        map(books => books.length)
      ).subscribe(numberofBooks => {
        this.infoHostDataSourceService.replace(new InfoHostItem(LibraryInfoComponent, {libraryName: this.libraryName, numberOfBooks: numberofBooks}));
      })
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

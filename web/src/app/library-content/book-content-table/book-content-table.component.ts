import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {faChevronDown, faChevronUp, IconDefinition} from '@fortawesome/free-solid-svg-icons';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {InfoHostDataSourceService} from '../control-bar/info-host-data-source/info-host-data-source.service';
import {InfoHostItem} from '../control-bar/info-host/info-host-item';
import {LibraryInfoComponent} from '../control-bar/library-info/library-info.component';

@Component({
  selector: 'app-book-content-table',
  templateUrl: './book-content-table.component.html',
  styleUrls: ['./book-content-table.component.scss']
})
export class BookContentTableComponent implements OnInit {
  showDetails: object = {};
  editDetails: object = {};

  @Input() libraryData$;

  libraryName: string;

  tableData$: Observable<any>;
  tableData: any;

  @Output() contentChanged = new EventEmitter();

  constructor(
    private infoHostDataSourceService: InfoHostDataSourceService
  ) { }

  ngOnInit() {
    this.libraryData$.subscribe(libraryData => {
      this.libraryName = libraryData.libraryName;
      this.tableData$ = libraryData.books$;

      this.tableData$.pipe(
        map(books => {
          this.tableData = books;
          return books.length
        })
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
    this.editDetails[rowId] = !this.editDetails[rowId];
  }

  isEditDetails(rowId: string): boolean {
    return this.editDetail[rowId];
  }

  finishEdit(rowId: string, updatedBook: any) {
    this.editDetail[rowId] = false;
    
    if (updatedBook == null) {
      return;
    }
    
    const index = this.tableData.findIndex(book => book.id === updatedBook.id);
    this.tableData[index] = updatedBook;
  }

  handleContentChanged() {
    this.contentChanged.next();
  }
}

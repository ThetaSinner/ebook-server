import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Observable} from "rxjs";
import {faChevronDown, faChevronUp, IconDefinition} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-video-summary-table',
  templateUrl: './video-summary-table.component.html',
  styleUrls: ['./video-summary-table.component.scss']
})
export class VideoSummaryTableComponent implements OnInit {
  @Input() videos$: Observable<any>;
  @Input() libraryName: string;

  @Output() contentChanged = new EventEmitter();

  showDetails: object = {};
  editDetails: object = {};
  private videos: any;

  constructor() { }

  ngOnInit() {
    this.videos$.subscribe(result => this.videos = result );
  }

  getIcon(rowId: string): IconDefinition {
    if (this.showDetails[rowId]) {
      return faChevronUp;
    }

    return faChevronDown;
  }

  toggleDetails(rowId: string) {
    this.showDetails[rowId] = !this.showDetails[rowId];
  }

  isShowDetails(rowId: string): boolean {
    return this.showDetails[rowId];
  }

  editDetail(rowId: string) {
    this.editDetails[rowId] = !this.editDetails[rowId];
  }

  isEditDetails(rowId: string): boolean {
    return this.editDetails[rowId];
  }

  finishEdit(rowId: string, updatedBook: any) {
    this.editDetails[rowId] = false;

    if (updatedBook == null) {
      return;
    }

    const index = this.videos.findIndex(book => book.id === updatedBook.id);
    this.videos[index] = updatedBook;
  }

  handleContentChanged() {
    this.contentChanged.next();
  }
}

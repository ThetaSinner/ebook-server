import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Router } from '@angular/router';
import { IconDefinition } from '@fortawesome/fontawesome-svg-core';
import { faBookmark, faPen, faTrashAlt } from '@fortawesome/free-solid-svg-icons';
import { environment } from 'src/environments/environment';
import { BookDataService } from '../book-data/book-data.service';

@Component({
  selector: 'app-content-detail',
  templateUrl: './content-detail.component.html',
  styleUrls: ['./content-detail.component.scss']
})
export class ContentDetailComponent implements OnInit {
  faEditIcon: IconDefinition = faPen;
  faDeleteIcon: IconDefinition = faTrashAlt;
  readIcon: IconDefinition = faBookmark;

  @Input() detailData;
  @Input() libraryName;

  @Output() startEdit = new EventEmitter();
  @Output() contentChanged = new EventEmitter();

  constructor(
    private router: Router,
    private bookDataService: BookDataService
  ) { }

  ngOnInit() {
  }

  getAuthorsWithCommas(authors: any[]) {
    return authors.join(', ').split(' ');
  }

  getImgSource() {
    return `${environment.mediaServerUrlBase}/books/${this.detailData.id}/covers?name=${this.libraryName}`;
  }

  handleEdit() {
    this.startEdit.emit('');
  }

  handleDelete() {
    const sub = this.bookDataService.deleteBook(this.detailData.id, this.libraryName).subscribe(() => {
      this.contentChanged.next();
      sub.unsubscribe();
    });
  }

  get readLink() {
    return `${environment.mediaServerUrlBase}/books/${this.detailData.title}?name=${this.libraryName}&id=${this.detailData.id}`;
  }
}

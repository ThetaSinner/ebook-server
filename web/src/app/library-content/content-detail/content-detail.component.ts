import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { IconDefinition } from '@fortawesome/fontawesome-svg-core';
import { faPen } from '@fortawesome/free-solid-svg-icons';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-content-detail',
  templateUrl: './content-detail.component.html',
  styleUrls: ['./content-detail.component.scss']
})
export class ContentDetailComponent implements OnInit {
  faEditIcon: IconDefinition = faPen;

  @Input() detailData;
  @Input() libraryName;

  @Output() startEdit = new EventEmitter();

  constructor() { }

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
}

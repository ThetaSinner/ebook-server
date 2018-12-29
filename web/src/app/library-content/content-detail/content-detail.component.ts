import { Component, OnInit, Input } from '@angular/core';
import { environment } from 'src/environments/environment';
import { LibraryContentWorkspaceComponent } from '../library-content-workspace/library-content-workspace.component';

@Component({
  selector: 'app-content-detail',
  templateUrl: './content-detail.component.html',
  styleUrls: ['./content-detail.component.scss']
})
export class ContentDetailComponent implements OnInit {
  @Input() detailData;
  @Input() libraryName;

  constructor() { }

  ngOnInit() {
  }

  getAuthorsWithCommas(authors: any[]) {
    return authors.join(', ').split(' ');
  }

  getImgSource() {
    return `${environment.mediaServerUrlBase}/books/${this.detailData.id}/covers?name=${this.libraryName}`;
  }
}

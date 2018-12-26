import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-content-detail',
  templateUrl: './content-detail.component.html',
  styleUrls: ['./content-detail.component.scss']
})
export class ContentDetailComponent implements OnInit {
  @Input() detailData;

  constructor() { }

  ngOnInit() {
  }

  getAuthorsWithCommas(authors: any[]) {
    return authors.join(', ').split(' ');
  }
}

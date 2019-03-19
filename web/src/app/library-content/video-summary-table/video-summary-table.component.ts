import {Component, Input, OnInit} from '@angular/core';
import {Observable} from "rxjs";

@Component({
  selector: 'app-video-summary-table',
  templateUrl: './video-summary-table.component.html',
  styleUrls: ['./video-summary-table.component.scss']
})
export class VideoSummaryTableComponent implements OnInit {
  @Input() videos$: Observable<any>;

  constructor() { }

  ngOnInit() {
    this.videos$.subscribe(result => console.log(result));
  }

}

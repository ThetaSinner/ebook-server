import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-content-table',
  templateUrl: './content-table.component.html',
  styleUrls: ['./content-table.component.scss']
})
export class ContentTableComponent implements OnInit {
  @Input() tableData$;

  constructor() { }

  ngOnInit() {
  }

}

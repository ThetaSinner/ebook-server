import { Component, OnInit, Input } from '@angular/core';
import { faChevronDown, IconDefinition } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-content-table',
  templateUrl: './content-table.component.html',
  styleUrls: ['./content-table.component.scss']
})
export class ContentTableComponent implements OnInit {
  faChevronDown: IconDefinition = faChevronDown;

  @Input() tableData$;

  constructor() { }

  ngOnInit() {
  }

}

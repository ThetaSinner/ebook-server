import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-missing-book-fix',
  templateUrl: './missing-book-fix.component.html',
  styleUrls: ['./missing-book-fix.component.scss']
})
export class MissingBookFixComponent implements OnInit {
  @Input() fixMissingBooks: any;
  @Input() libraryName: string;

  constructor() { }

  ngOnInit() {
  }

}

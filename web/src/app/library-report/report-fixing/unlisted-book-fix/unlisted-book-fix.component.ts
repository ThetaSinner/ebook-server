import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-unlisted-book-fix',
  templateUrl: './unlisted-book-fix.component.html',
  styleUrls: ['./unlisted-book-fix.component.scss']
})
export class UnlistedBookFixComponent implements OnInit {
  @Input() fixUnlistedBooks: any;
  @Input() libraryName: string;

  @Output() fieldFix: EventEmitter<any> = new EventEmitter();

  constructor() { }

  ngOnInit() {
  }

  removeUnlistedBook(unlistedBook: any) {
    this.fieldFix.emit({
      reportFixFieldId: unlistedBook.issueId,
      fixAction: 'RemoveUnlisted'
    });
  }

  addUnlistedBook(unlistedBook: any) {
    this.fieldFix.emit({
      reportFixFieldId: unlistedBook.issueId,
      fixAction: 'AddUnlisted'
    });
  }
}

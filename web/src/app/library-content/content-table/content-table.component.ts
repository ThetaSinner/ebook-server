import { Component, Input, OnInit } from '@angular/core';
import { faChevronDown, faChevronUp, IconDefinition } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-content-table',
  templateUrl: './content-table.component.html',
  styleUrls: ['./content-table.component.scss']
})
export class ContentTableComponent implements OnInit {
  faExpandContractIcon: IconDefinition = faChevronDown;
  showDetails: boolean = false;

  @Input() tableData$;

  constructor() { }

  ngOnInit() {
  }

  getTags(book: any) {
    if (book && book.metadata) {
      return book.metadata.tags;
    }

    return null;
  }

  getRating(book: any) {
    if (book && book.metadata) {
      return book.metadata.rating;
    }

    return null;
  }

  toggleDetails() {
    this.showDetails = !this.showDetails;

    if (this.showDetails) {
      this.faExpandContractIcon = faChevronUp;
    }
    else {
      this.faExpandContractIcon = faChevronDown;
    }
  }
}

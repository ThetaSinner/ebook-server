import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { BookDataService } from '../book-data/book-data.service';

@Component({
  selector: 'app-library-content-workspace',
  templateUrl: './library-content-workspace.component.html',
  styleUrls: ['./library-content-workspace.component.scss']
})
export class LibraryContentWorkspaceComponent implements OnInit {
  books: any;

  constructor(
    private route: ActivatedRoute,
    private bookDataService: BookDataService
  ) { }

  ngOnInit() {
    this.books = this.route.paramMap.pipe(
      switchMap((params: ParamMap) =>
        this.bookDataService.getBooks(params.get('libraryName')))
    );
  }
}

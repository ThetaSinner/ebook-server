import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { BookDataService } from '../book-data/book-data.service';
import { InfoHostDataSourceService } from '../control-bar/info-host-data-source/info-host-data-source.service';
import { InfoHostItem } from '../control-bar/info-host/info-host-item';

@Component({
  selector: 'app-library-content-workspace',
  templateUrl: './library-content-workspace.component.html',
  styleUrls: ['./library-content-workspace.component.scss']
})
export class LibraryContentWorkspaceComponent implements OnInit {
  books$: any;
  infoHostItems: InfoHostItem[];

  constructor(
    private route: ActivatedRoute,
    private bookDataService: BookDataService,
    private infoHostDataSourceService: InfoHostDataSourceService
  ) { }

  ngOnInit() {
    this.books$ = this.route.paramMap.pipe(
      switchMap((params: ParamMap) =>
        this.bookDataService.getBooks(params.get('libraryName'))
      )
    );

    this.infoHostItems = this.infoHostDataSourceService.getInfoItems();
  }
}

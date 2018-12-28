import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { BookDataService } from '../book-data/book-data.service';
import { InfoHostDataSourceService } from '../control-bar/info-host-data-source/info-host-data-source.service';
import { InfoHostItem } from '../control-bar/info-host/info-host-item';
import { LibraryInfoComponent } from '../control-bar/library-info/library-info.component';

@Component({
  selector: 'app-library-content-workspace',
  templateUrl: './library-content-workspace.component.html',
  styleUrls: ['./library-content-workspace.component.scss']
})
export class LibraryContentWorkspaceComponent implements OnInit {
  books$: any;
  infoItems: InfoHostItem[];

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

    this.infoHostDataSourceService.getItemsStream().subscribe(value => {
      this.infoItems = value;
    });

    this.infoHostDataSourceService.replace(new InfoHostItem(LibraryInfoComponent, {libraryName: 'test', numberOfBooks: 10}));

    setTimeout(() => {
      this.infoHostDataSourceService.replace(new InfoHostItem(LibraryInfoComponent, {libraryName: 'test', numberOfBooks: 11}));
    }, 3000);
  }
}

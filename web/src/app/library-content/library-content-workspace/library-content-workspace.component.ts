import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { BookDataService } from '../book-data/book-data.service';
import { ChangeInfoComponent } from '../control-bar/change-info/change-info.component';
import { InfoHostDataSourceService } from '../control-bar/info-host-data-source/info-host-data-source.service';
import { InfoHostItem } from '../control-bar/info-host/info-host-item';
import { LibraryInfoComponent } from '../control-bar/library-info/library-info.component';

@Component({
  selector: 'app-library-content-workspace',
  templateUrl: './library-content-workspace.component.html',
  styleUrls: ['./library-content-workspace.component.scss']
})
export class LibraryContentWorkspaceComponent implements OnInit {
  libraryData$: any;
  infoItems: InfoHostItem[];
  private libraryName: string;

  constructor(
    private route: ActivatedRoute,
    private bookDataService: BookDataService,
    private infoHostDataSourceService: InfoHostDataSourceService
  ) { }

  ngOnInit() {
    
    this.libraryData$ = this.route.paramMap.pipe(
      switchMap((params: ParamMap) => {
        const libraryName = params.get('libraryName');
        this.libraryName = libraryName;
        
        this.bookDataService.doGetBooks(libraryName);

        return of({
          books$: this.bookDataService.getBooks(libraryName),
          libraryName: libraryName 
        });
      })
    );

    this.infoHostDataSourceService.getItemsStream().subscribe(value => {
      this.infoItems = value;
    });

    this.infoHostDataSourceService.replace(new InfoHostItem(LibraryInfoComponent, {libraryName: 'test', numberOfBooks: 10}));

    setTimeout(() => {
      this.infoHostDataSourceService.replace(new InfoHostItem(LibraryInfoComponent, {libraryName: 'test', numberOfBooks: 11}));
    }, 3000);

    this.infoHostDataSourceService.replace(new InfoHostItem(ChangeInfoComponent, {numberOfBooksAdded: 2, numberOfBooksDeleted: 1, numberOfBooksChanged: 5}))
  }

  handleContentChanged() {
    this.bookDataService.doGetBooks(this.libraryName);
  }
}

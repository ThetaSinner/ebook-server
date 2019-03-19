import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, ParamMap} from '@angular/router';
import {of} from 'rxjs';
import {switchMap} from 'rxjs/operators';
import {ChangeTracker, ChangeType,} from 'src/app/library-change/change-tracker';
import {LibraryChangeService} from 'src/app/library-change/library-change.service';
import {LibraryDataService} from 'src/app/library/library-data/library-data.service';
import {BookDataService} from '../../service/book-data/book-data.service';
import {ChangeInfoComponent} from '../control-bar/change-info/change-info.component';
import {InfoHostDataSourceService} from '../control-bar/info-host-data-source/info-host-data-source.service';
import {InfoHostItem} from '../control-bar/info-host/info-host-item';
import {VideoDataService} from "../../service/video-data/video-data.service";

@Component({
  selector: 'app-library-content-workspace',
  templateUrl: './library-content-workspace.component.html',
  styleUrls: ['./library-content-workspace.component.scss']
})
export class LibraryContentWorkspaceComponent implements OnInit {
  libraryData$: any;
  infoItems: InfoHostItem[];
  private libraryName: string;
  private changeTracker: ChangeTracker | null;
  // will reset on page load. Is that okay?
  private changeState = {
    numberOfBooksAdded: 0,
    numberOfBooksDeleted: 0,
    numberOfBooksChanged: 0
  };

  constructor(
    private route: ActivatedRoute,
    private bookDataService: BookDataService,
    private infoHostDataSourceService: InfoHostDataSourceService,
    private libraryDataService: LibraryDataService,
    private libraryChangeService: LibraryChangeService,
    private videoDataService: VideoDataService
  ) { }

  ngOnInit() {
    this.libraryData$ = this.route.paramMap.pipe(
      switchMap((params: ParamMap) => {
        const libraryName = params.get('libraryName');
        this.libraryName = libraryName;
        
        this.bookDataService.doGetBooks(libraryName);

        this.videoDataService.doGetVideos(libraryName);

        this.changeTracker = this.libraryChangeService.listen(this.libraryName);
        this.changeTracker.changes.subscribe(change => {
          switch (change.changeType) {
            case ChangeType.BookCreated:
              this.changeState.numberOfBooksAdded++;
              break;
            case ChangeType.BookDeleted:
              this.changeState.numberOfBooksDeleted++;
              break;
            case ChangeType.BookUpdated:
              this.changeState.numberOfBooksChanged++;
              break;
          }

          // can filter some changes by bookId.

          this.infoHostDataSourceService.replace(new InfoHostItem(ChangeInfoComponent, this.changeState));
        });

        return of({
          books$: this.bookDataService.getBooks(libraryName),
          videos$: this.videoDataService.getVideos(libraryName),
          libraryName: libraryName 
        });
      })
    );

    this.infoHostDataSourceService.getItemsStream().subscribe(value => {
      this.infoItems = value;
    });
  }

  handleContentChanged() {
    this.bookDataService.doGetBooks(this.libraryName);
  }

  handleSaveRequested() {
    const sub = this.libraryDataService.saveLibrary(this.libraryName).subscribe(() => {
      sub.unsubscribe();
    });
  }
}

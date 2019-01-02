import { Component, OnInit } from '@angular/core';
import { BookDataService } from 'src/app/library-content/book-data/book-data.service';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-book-upload',
  templateUrl: './book-upload.component.html',
  styleUrls: ['./book-upload.component.scss']
})
export class BookUploadComponent implements OnInit {
  private files: any;
  private libraryName: string;

  constructor(
    private activeRoute: ActivatedRoute,
    private bookDataService: BookDataService
  ) { }

  ngOnInit() {
    this.activeRoute.paramMap.pipe(
      map((params: ParamMap) => params.get('libraryName'))
    ).subscribe(libraryName => {
      this.libraryName = libraryName;
    });
  }

  handleFilesSelected(files: any) {
    this.files = files;
  }

  handleUpload() {
    this.bookDataService.uploadBooks(this.files, this.libraryName);
  }
}

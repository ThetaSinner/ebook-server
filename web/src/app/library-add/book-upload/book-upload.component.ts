import { Component, OnInit } from '@angular/core';
import { BookDataService } from 'src/app/service/book-data/book-data.service';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { map } from 'rxjs/operators';
import { FormGroup, FormControl } from '@angular/forms';

@Component({
  selector: 'app-book-upload',
  templateUrl: './book-upload.component.html',
  styleUrls: ['./book-upload.component.scss']
})
export class BookUploadComponent implements OnInit {
  private files: any;
  private libraryName: string;
  uploadForm: FormGroup = new FormGroup({
    files: new FormControl('')
  });

  constructor(
    private activeRoute: ActivatedRoute,
    private bookDataService: BookDataService
  ) { }

  ngOnInit() {
    this.activeRoute.parent.paramMap.pipe(
      map((params: ParamMap) => params.get('libraryName'))
    ).subscribe(libraryName => {
      this.libraryName = libraryName;
    });
  }

  handleFilesSelected(files: any) {
    this.files = files;
  }

  handleUpload() {
    const sub = this.bookDataService.uploadBooks(this.files, this.libraryName).subscribe(() => {
      this.uploadForm.setControl('files', new FormControl(''))
      sub.unsubscribe();
    });
  }
}

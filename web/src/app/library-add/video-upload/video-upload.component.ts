import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, ParamMap} from "@angular/router";
import {map} from "rxjs/operators";
import {VideoDataService} from "../../service/video-data/video-data.service";

@Component({
  selector: 'app-video-upload',
  templateUrl: './video-upload.component.html',
  styleUrls: ['./video-upload.component.scss']
})
export class VideoUploadComponent implements OnInit {
  files: FileList;
  private libraryName: string;

  constructor(
    private activeRoute: ActivatedRoute,
    private videoDataService: VideoDataService
  ) { }

  ngOnInit() {
    this.activeRoute.parent.paramMap.pipe(
      map((params: ParamMap) => params.get('libraryName'))
    ).subscribe(libraryName => {
      this.libraryName = libraryName;
    });
  }

  handleFilesSelected(files: FileList) {
    this.files = files;
  }

  handleUpload() {
    const sub = this.videoDataService.uploadVideos(this.files, this.libraryName).subscribe(() => {
      // TODO this.uploadForm.setControl('files', new FormControl(''))
      sub.unsubscribe();
    });
  }
}

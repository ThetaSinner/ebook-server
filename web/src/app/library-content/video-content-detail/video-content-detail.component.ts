import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {faBookmark, faPen, faTrashAlt, IconDefinition} from "@fortawesome/free-solid-svg-icons";
import {Router} from "@angular/router";
import {environment} from "../../../environments/environment";
import {VideoDataService} from "../../service/video-data/video-data.service";

@Component({
  selector: 'app-video-content-detail',
  templateUrl: './video-content-detail.component.html',
  styleUrls: ['./video-content-detail.component.scss']
})
export class VideoContentDetailComponent implements OnInit {
  faEditIcon: IconDefinition = faPen;
  faDeleteIcon: IconDefinition = faTrashAlt;
  readIcon: IconDefinition = faBookmark;

  @Input() video;
  @Input() libraryName;

  @Output() startEdit = new EventEmitter();
  @Output() contentChanged = new EventEmitter();

  constructor(
    private router: Router,
    private videoDataService: VideoDataService
  ) { }

  ngOnInit() {
  }

  getImgSource() {
    return `${environment.mediaServerUrlBase}/videos/${this.video.id}/thumbnail?name=${this.libraryName}`;
  }

  handleEdit() {
    this.startEdit.emit('');
  }

  handleDelete() {
    const sub = this.videoDataService.deleteBook(this.video.id, this.libraryName).subscribe(() => {
      this.contentChanged.next();
      sub.unsubscribe();
    });
  }

  get readLink() {
    if (this.video.url && this.video.url.type === 'WebLink') {
      return this.video.url.value;
    }
    else {
      return VideoDataService.getPlaybackUrl(this.libraryName, this.video.id);
    }
  }}

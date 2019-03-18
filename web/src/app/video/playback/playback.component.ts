import {Component, Input, OnInit} from '@angular/core';
import {VideoDataService} from "../../service/video-data/video-data.service";

@Component({
  selector: 'app-playback',
  templateUrl: './playback.component.html',
  styleUrls: ['./playback.component.scss']
})
export class PlaybackComponent implements OnInit {
  @Input() libraryName: string;
  @Input() video: any;

  playbackUrl: string;

  constructor() { }

  ngOnInit() {
    this.playbackUrl = VideoDataService.getPlaybackUrl(this.libraryName, this.video.id);
  }

}

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {faCheck, faMinus, faPlus, faTimesCircle, IconDefinition} from "@fortawesome/free-solid-svg-icons";
import {FormArray, FormBuilder, FormGroup} from "@angular/forms";
import {NgbCalendar, NgbDate} from "@ng-bootstrap/ng-bootstrap";
import {compare} from "fast-json-patch";
import {VideoDataService} from "../../service/video-data/video-data.service";

@Component({
  selector: 'app-video-content-edit',
  templateUrl: './video-content-edit.component.html',
  styleUrls: ['./video-content-edit.component.scss']
})
export class VideoContentEditComponent implements OnInit {
  addIcon: IconDefinition = faPlus;
  removeIcon: IconDefinition = faMinus;
  saveChangesIcon: IconDefinition = faCheck;
  cancelChangesIcon: IconDefinition = faTimesCircle;

  detailForm: FormGroup;
  @Input() detailData;
  @Input() libraryName: string;

  @Output() editFinished = new EventEmitter();

  constructor(
    private formBuilder: FormBuilder,
    private videoDataService: VideoDataService,
    private calendar: NgbCalendar
  ) { }

  ngOnInit() {
    const metadata: any = {};
    if (this.detailData.metadata) {
      metadata.rating = this.detailData.metadata.rating;
      metadata.tags = this.detailData.metadata.tags;
    }

    let tags = [
      this.formBuilder.control('')
    ];
    if (metadata.tags && metadata.tags.length) {
      tags = metadata.tags.map((tag: string) =>
        this.formBuilder.control(tag)
      );
    }

    const releaseDate: NgbDate = this.calendar.getToday();
    if (this.detailData.releaseDate) {
      const savedReleaseDate = new Date(this.detailData.releaseDate);

      releaseDate.year = savedReleaseDate.getFullYear();
      releaseDate.month = savedReleaseDate.getMonth() + 1;
      releaseDate.day = savedReleaseDate.getDate();
    }

    this.detailForm = this.formBuilder.group({
      title: [this.detailData.title ? this.detailData.title : ''],
      description: [this.detailData.publisher ? this.detailData.publisher : ''],
      rating: [metadata.rating ? metadata.rating : ''],
      releaseDate: [releaseDate],
      tags: this.formBuilder.array(tags)
    });
  }

  get tagsControl() {
    return this.detailForm.get('tags') as FormArray;
  }

  addTagInput() {
    this.tagsControl.push(this.formBuilder.control(''));
  }

  removeTagInput(index: number) {
    this.tagsControl.removeAt(index);
  }

  saveChanges() {
    const formOutput = this.detailForm.getRawValue();

    // Create update request with the fields that can't be edited.
    const updatedVideo: any = {
      id: this.detailData.id,
      url: this.detailData.url
    };

    if (formOutput.title) {
      updatedVideo.title = formOutput.title;
    }
    if (formOutput.description) {
      updatedVideo.description = formOutput.description;
    }
    if (formOutput.releaseDate) {
      const dateStruct = formOutput.releaseDate;
      updatedVideo.releaseDate = new Date(dateStruct.year, dateStruct.month - 1, dateStruct.day);
    }

    if (formOutput.rating || VideoContentEditComponent.isArrayValid(formOutput, 'tags')) {
      updatedVideo.metadata = {};

      if (formOutput.rating) {
        updatedVideo.metadata.rating = formOutput.rating;
      }

      if (VideoContentEditComponent.isArrayValid(formOutput, 'tags')) {
        updatedVideo.metadata.tags = formOutput.tags.filter(tag => tag);
      }
    }

    const diff = compare(this.detailData, updatedVideo);

    const sub = this.videoDataService.updateVideo(this.detailData.id, diff, this.libraryName).subscribe((updatedVideo) => {
      this.finishEdit(updatedVideo);
      sub.unsubscribe();
    });
  }

  private static isArrayValid(objectToTest: object, field: string) {
    if (objectToTest[field]) {
      const array = objectToTest[field].filter((val: any) => val);

      return array.length
    }

    return false;
  }

  private finishEdit(updatedBook) {
    this.editFinished.emit(updatedBook);
  }

  cancelEdit() {
    this.editFinished.emit(null);
  }

  get isbn() {
    return this.detailForm.get('isbn');
  }
}

import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {VideoSummaryTableComponent} from './video-summary-table.component';

describe('VideoSummaryTableComponent', () => {
  let component: VideoSummaryTableComponent;
  let fixture: ComponentFixture<VideoSummaryTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VideoSummaryTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VideoSummaryTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {VideoContentDetailComponent} from './video-content-detail.component';

describe('VideoContentDetailComponent', () => {
  let component: VideoContentDetailComponent;
  let fixture: ComponentFixture<VideoContentDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VideoContentDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VideoContentDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

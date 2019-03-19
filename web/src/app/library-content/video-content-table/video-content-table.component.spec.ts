import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {VideoContentTableComponent} from './video-content-table.component';

describe('VideoContentTableComponent', () => {
  let component: VideoContentTableComponent;
  let fixture: ComponentFixture<VideoContentTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VideoContentTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VideoContentTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

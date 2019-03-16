import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VideoRemoteAddComponent } from './video-remote-add.component';

describe('VideoRemoteAddComponent', () => {
  let component: VideoRemoteAddComponent;
  let fixture: ComponentFixture<VideoRemoteAddComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VideoRemoteAddComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VideoRemoteAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VideoContentEditComponent } from './video-content-edit.component';

describe('VideoContentEditComponent', () => {
  let component: VideoContentEditComponent;
  let fixture: ComponentFixture<VideoContentEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VideoContentEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VideoContentEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

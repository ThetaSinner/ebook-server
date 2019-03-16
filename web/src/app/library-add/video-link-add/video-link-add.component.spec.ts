import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VideoLinkAddComponent } from './video-link-add.component';

describe('VideoLinkAddComponent', () => {
  let component: VideoLinkAddComponent;
  let fixture: ComponentFixture<VideoLinkAddComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VideoLinkAddComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VideoLinkAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

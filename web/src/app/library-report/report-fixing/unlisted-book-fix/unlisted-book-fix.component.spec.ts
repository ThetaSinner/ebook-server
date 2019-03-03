import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UnlistedBookFixComponent } from './unlisted-book-fix.component';

describe('UnlistedBookFixComponent', () => {
  let component: UnlistedBookFixComponent;
  let fixture: ComponentFixture<UnlistedBookFixComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UnlistedBookFixComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UnlistedBookFixComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

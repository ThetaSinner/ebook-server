import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MissingBookFixComponent } from './missing-book-fix.component';

describe('MissingBookFixComponent', () => {
  let component: MissingBookFixComponent;
  let fixture: ComponentFixture<MissingBookFixComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MissingBookFixComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MissingBookFixComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

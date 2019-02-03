import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LocalBookFixComponent } from './local-book-fix.component';

describe('LocalBookFixComponent', () => {
  let component: LocalBookFixComponent;
  let fixture: ComponentFixture<LocalBookFixComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LocalBookFixComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LocalBookFixComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

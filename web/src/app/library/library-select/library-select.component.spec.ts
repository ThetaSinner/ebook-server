import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LibrarySelectComponent } from './library-select.component';

describe('LibrarySelectComponent', () => {
  let component: LibrarySelectComponent;
  let fixture: ComponentFixture<LibrarySelectComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LibrarySelectComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LibrarySelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

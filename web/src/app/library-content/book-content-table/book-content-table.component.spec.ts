import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {BookContentTableComponent} from './book-content-table.component';

describe('BookContentTableComponent', () => {
  let component: BookContentTableComponent;
  let fixture: ComponentFixture<BookContentTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BookContentTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BookContentTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

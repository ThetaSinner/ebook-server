import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BookRemoteAddComponent } from './book-remote-add.component';

describe('BookRemoteAddComponent', () => {
  let component: BookRemoteAddComponent;
  let fixture: ComponentFixture<BookRemoteAddComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BookRemoteAddComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BookRemoteAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

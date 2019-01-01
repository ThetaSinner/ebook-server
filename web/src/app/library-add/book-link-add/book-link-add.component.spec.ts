import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BookLinkAddComponent } from './book-link-add.component';

describe('BookLinkAddComponent', () => {
  let component: BookLinkAddComponent;
  let fixture: ComponentFixture<BookLinkAddComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BookLinkAddComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BookLinkAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

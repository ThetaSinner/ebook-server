import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LibraryWorkspaceComponent } from './library-workspace.component';

describe('LibraryWorkspaceComponent', () => {
  let component: LibraryWorkspaceComponent;
  let fixture: ComponentFixture<LibraryWorkspaceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LibraryWorkspaceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LibraryWorkspaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

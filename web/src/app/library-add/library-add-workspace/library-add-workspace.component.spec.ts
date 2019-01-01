import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LibraryAddWorkspaceComponent } from './library-add-workspace.component';

describe('LibraryAddWorkspaceComponent', () => {
  let component: LibraryAddWorkspaceComponent;
  let fixture: ComponentFixture<LibraryAddWorkspaceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LibraryAddWorkspaceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LibraryAddWorkspaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

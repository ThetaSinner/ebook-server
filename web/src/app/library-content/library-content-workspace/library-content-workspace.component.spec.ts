import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LibraryContentWorkspaceComponent } from './library-content-workspace.component';

describe('LibraryContentWorkspaceComponent', () => {
  let component: LibraryContentWorkspaceComponent;
  let fixture: ComponentFixture<LibraryContentWorkspaceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LibraryContentWorkspaceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LibraryContentWorkspaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

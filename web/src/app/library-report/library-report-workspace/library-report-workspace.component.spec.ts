import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LibraryReportWorkspaceComponent } from './library-report-workspace.component';

describe('LibraryReportWorkspaceComponent', () => {
  let component: LibraryReportWorkspaceComponent;
  let fixture: ComponentFixture<LibraryReportWorkspaceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LibraryReportWorkspaceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LibraryReportWorkspaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

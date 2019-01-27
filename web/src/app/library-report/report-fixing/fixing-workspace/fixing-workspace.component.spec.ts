import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FixingWorkspaceComponent } from './fixing-workspace.component';

describe('FixingWorkspaceComponent', () => {
  let component: FixingWorkspaceComponent;
  let fixture: ComponentFixture<FixingWorkspaceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FixingWorkspaceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FixingWorkspaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

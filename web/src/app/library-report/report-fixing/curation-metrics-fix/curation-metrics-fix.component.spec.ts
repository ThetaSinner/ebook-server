import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CurationMetricsFixComponent } from './curation-metrics-fix.component';

describe('CurationMetricsFixComponent', () => {
  let component: CurationMetricsFixComponent;
  let fixture: ComponentFixture<CurationMetricsFixComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CurationMetricsFixComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CurationMetricsFixComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

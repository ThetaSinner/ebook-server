import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CurationCompletionChartComponent } from './curation-completion-chart.component';

describe('CurationCompletionChartComponent', () => {
  let component: CurationCompletionChartComponent;
  let fixture: ComponentFixture<CurationCompletionChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CurationCompletionChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CurationCompletionChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ContentBreakdownChartComponent } from './content-breakdown-chart.component';

describe('ContentBreakdownChartComponent', () => {
  let component: ContentBreakdownChartComponent;
  let fixture: ComponentFixture<ContentBreakdownChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ContentBreakdownChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContentBreakdownChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

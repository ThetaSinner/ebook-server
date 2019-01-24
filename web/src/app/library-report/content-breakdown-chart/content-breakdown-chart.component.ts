import { Component, Input, OnInit, ViewChild, ElementRef } from '@angular/core';
import { Observable } from 'rxjs';
import Chart from 'chart.js';

@Component({
  selector: 'app-content-breakdown-chart',
  templateUrl: './content-breakdown-chart.component.html',
  styleUrls: ['./content-breakdown-chart.component.scss']
})
export class ContentBreakdownChartComponent implements OnInit {
  @ViewChild('breakdownChart') breakdownChartRef: ElementRef;

  @Input() metrics$: Observable<any>;

  chart: any;

  constructor() { }

  ngOnInit() {
    const sub = this.metrics$.subscribe(metrics => {
      this.buildChart(metrics);
      sub.unsubscribe();
    });
  }

  private buildChart(metrics) {
    const chartData = [
      metrics.numberOfWebLinkBooks,
      metrics.numberOfLocalManagedBooks,
      metrics.numberOfLocalUnmanagedBooks
    ];

    const chartLabels = [
      'Links',
      'Managed Books',
      'Unmanaged Books'
    ];

    var ctx = this.breakdownChartRef.nativeElement;
    this.chart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        datasets: [{
          data: chartData,
          backgroundColor: [
            'rgba(100, 0, 0, 0.5)',
            'rgba(0, 100, 0, 0.5)',
            'rgba(0, 0, 100, 0.5)'
          ]
        }],
        labels: chartLabels
      }
    });
  }
}

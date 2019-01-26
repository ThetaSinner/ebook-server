import { Component, Input, OnInit, ViewChild, ElementRef, OnChanges, SimpleChanges } from '@angular/core';
import Chart from 'chart.js';

@Component({
  selector: 'app-content-breakdown-chart',
  templateUrl: './content-breakdown-chart.component.html',
  styleUrls: ['./content-breakdown-chart.component.scss']
})
export class ContentBreakdownChartComponent implements OnInit, OnChanges {
  @ViewChild('breakdownChart') breakdownChartRef: ElementRef;

  @Input() metrics: any;

  private chart: any;

  constructor() { }

  ngOnInit() {
    if (this.metrics) {
      this.buildChart(this.metrics);
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.metrics.currentValue) {
      this.buildChart(changes.metrics.currentValue);
    }
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

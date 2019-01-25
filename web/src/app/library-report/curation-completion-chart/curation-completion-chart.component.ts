import { Component, OnInit, Input, ViewChild, ElementRef } from '@angular/core';
import { Observable } from 'rxjs';
import Chart from 'chart.js';

@Component({
  selector: 'app-curation-completion-chart',
  templateUrl: './curation-completion-chart.component.html',
  styleUrls: ['./curation-completion-chart.component.scss']
})
export class CurationCompletionChartComponent implements OnInit {
  @ViewChild('curationCompletionChart') curationCompletionChartRef: ElementRef;

  @Input() curationData$: Observable<any>;

  private chart: any;

  constructor() { }

  ngOnInit() {
    const sub = this.curationData$.subscribe(curationData => {
      this.buildChart(curationData);
      sub.unsubscribe();
    });
  }

  private buildChart(curationData) {
    console.log(curationData);
    console.log((curationData.booksWithMissingTitles.length / curationData.bookTotal) * 100);

    const chartData = [
      ((curationData.bookTotal - curationData.booksWithMissingTitles.length) / curationData.bookTotal) * 100,
      ((curationData.bookTotal - curationData.booksWithMissingPublisher.length) / curationData.bookTotal) * 100,
      ((curationData.bookTotal - curationData.booksWithMissingAuthors.length) / curationData.bookTotal) * 100
    ];

    const chartLabels = [
      'Titles',
      'Publishers',
      'Authors'
    ];

    var ctx = this.curationCompletionChartRef.nativeElement;
    this.chart = new Chart(ctx, {
      type: 'bar',
      data: {
        datasets: [{
          label: 'Curation completion',
          data: chartData,
          backgroundColor: [
            'rgba(100, 30, 0, 0.5)',
            'rgba(0, 100, 30, 0.5)',
            'rgba(30, 0, 100, 0.5)'
          ]
        }],
        labels: chartLabels
      },
      options: {
        legend: {
          display: false
        },
        scales: {
          yAxes: [{
            ticks: {
              beginAtZero: true,
              min: 0,
              max: 100
            }
          }]
        }
      }
    });
  }
}

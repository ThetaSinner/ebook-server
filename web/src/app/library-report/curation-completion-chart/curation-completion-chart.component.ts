import { Component, OnInit, Input, ViewChild, ElementRef, OnChanges, SimpleChanges } from '@angular/core';
import { Observable } from 'rxjs';
import Chart from 'chart.js';

@Component({
  selector: 'app-curation-completion-chart',
  templateUrl: './curation-completion-chart.component.html',
  styleUrls: ['./curation-completion-chart.component.scss']
})
export class CurationCompletionChartComponent implements OnInit, OnChanges {
  @ViewChild('curationCompletionChart') curationCompletionChartRef: ElementRef;

  @Input() curationData: any;

  private chart: any;

  constructor() { }

  ngOnInit() {
    if (this.curationData) {
      this.buildChart(this.curationData);
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.curationData.currentValue) {
      this.buildChart(changes.curationData.currentValue);
    }
  }

  private buildChart(curationData) {
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
        },
        onClick: (context) => {
          const clickedElement = this.chart.getElementAtEvent(context);
          
          if (!clickedElement.length) {
            return;
          }

          const columnIndex = clickedElement[0]._index;

          console.log(`Show completion helper for ${chartLabels[columnIndex]}`);
        }
      }
    });
  }
}

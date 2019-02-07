import { Component, OnInit, Input, ViewChild, ElementRef, OnChanges, SimpleChanges } from '@angular/core';
import { Observable } from 'rxjs';
import Chart from 'chart.js';

enum CurationDataFieldName {
  Title,
  Publisher,
  Authors
}

@Component({
  selector: 'app-curation-completion-chart',
  templateUrl: './curation-completion-chart.component.html',
  styleUrls: ['./curation-completion-chart.component.scss']
})
export class CurationCompletionChartComponent implements OnInit, OnChanges {
  @ViewChild('curationCompletionChart') curationCompletionChartRef: ElementRef;

  @Input() curationData: any;

  private chart: any;

  private chartSelectionState: CurationDataFieldName[] = [];

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

          this.toggleChartColumn(chartLabels[columnIndex]);
        }
      }
    });
  }

  private toggleChartColumn(name: string): any {
    const fieldName = this.getCurationDataFieldName(name);

    if (this.chartSelectionState.indexOf(fieldName) === -1) {
      this.chartSelectionState.push(fieldName);
    }
    else {
      this.chartSelectionState = this.chartSelectionState.filter(x => x !== fieldName);
    }

    console.log(this.chartSelectionState);
  }

  private getCurationDataFieldName(name: string): CurationDataFieldName {
    return {
      Titles: CurationDataFieldName.Title,
      Publishers: CurationDataFieldName.Publisher,
      Authors: CurationDataFieldName.Authors
    }[name];
  }
}

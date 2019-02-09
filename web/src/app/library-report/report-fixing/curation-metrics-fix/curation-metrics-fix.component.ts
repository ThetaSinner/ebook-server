import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-curation-metrics-fix',
  templateUrl: './curation-metrics-fix.component.html',
  styleUrls: ['./curation-metrics-fix.component.scss']
})
export class CurationMetricsFixComponent implements OnInit, OnChanges {
  @Input() curationMetrics: any;
  @Input() fixCurationFieldNames: any;
  @Input() libraryName: string;
  
  constructor() { }
  
  ngOnInit() {
    console.log('dingding!', this.curationMetrics, this.fixCurationFieldNames);
  }
  
  ngOnChanges(changes: SimpleChanges): void {
    console.log('changes', changes);

    if (changes.curationMetrics && changes.curationMetrics.currentValue) {
      this.curationMetrics = changes.curationMetrics.currentValue;
    }
    if (changes.fixCurationFieldNames && changes.fixCurationFieldNames.currentValue) {
      this.fixCurationFieldNames = changes.fixCurationFieldNames.currentValue;
    }

    console.log('dingding!', this.curationMetrics, this.fixCurationFieldNames);
  }
}

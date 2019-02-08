import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-curation-metrics-fix',
  templateUrl: './curation-metrics-fix.component.html',
  styleUrls: ['./curation-metrics-fix.component.scss']
})
export class CurationMetricsFixComponent implements OnInit {
  @Input() curationMetrics: any;
  @Input() fixCurationFieldNames: any;
  @Input() libraryName: string;

  constructor() { }

  ngOnInit() {
    console.log('dingding!', this.curationMetrics, this.fixCurationFieldNames);
  }

}

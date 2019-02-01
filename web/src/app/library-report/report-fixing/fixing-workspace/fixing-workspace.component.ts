import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-fixing-workspace',
  templateUrl: './fixing-workspace.component.html',
  styleUrls: ['./fixing-workspace.component.scss']
})
export class FixingWorkspaceComponent implements OnInit, OnChanges {
  @Input() reportFixModel: any;
  @Input() libraryName: string;
  fixWebLinks: any;

  constructor() { }

  ngOnInit() {
    this.renderFixingWorkspace();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.reportFixModel.currentValue) {
      this.reportFixModel = changes.reportFixModel.currentValue
      this.renderFixingWorkspace();
    }
  }

  renderFixingWorkspace() {
    if (!this.reportFixModel) {
      return;
    }

    const report = this.reportFixModel.report;
    if (!report) {
      return;
    }

    const unreachableBooksModel = report.unreachableBooksModel;
    if (unreachableBooksModel && unreachableBooksModel.webLinks) {
      this.fixWebLinks = unreachableBooksModel.webLinks;
    }
  }
}

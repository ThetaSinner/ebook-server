import { Component, OnInit, Input, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-fixing-workspace',
  templateUrl: './fixing-workspace.component.html',
  styleUrls: ['./fixing-workspace.component.scss']
})
export class FixingWorkspaceComponent implements OnInit, OnChanges {
  @Input() reportFixModel: any;
  @Input() libraryName: string;

  @Output() fieldFix: EventEmitter<any> = new EventEmitter();

  fixWebLinks: any;
  fixLocalBooks: any;
  fixMissingBooks: any;
  curationMetrics: any;
  fixUnlistedBooks: any;

  constructor() { }

  ngOnInit() {
    this.renderFixingWorkspace();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.reportFixModel && changes.reportFixModel.currentValue) {
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

    if (report.missingBooks) {
      this.fixMissingBooks = report.missingBooks;
    }

    if (report.unlistedBooks) {
      this.fixUnlistedBooks = report.unlistedBooks;
    }

    if (report.curationMetrics) {
      this.curationMetrics = report.curationMetrics;
    }

    const unreachableBooksModel = report.unreachableBooksModel;
    if (unreachableBooksModel && unreachableBooksModel.webLinks) {
      this.fixWebLinks = unreachableBooksModel.webLinks;
    }

    if (unreachableBooksModel && unreachableBooksModel.localBooks) {
      this.fixLocalBooks = unreachableBooksModel.localBooks;
    }
  }

  handleFieldFix($event) {
    this.fieldFix.emit($event);
  }
}

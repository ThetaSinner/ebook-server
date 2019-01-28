import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-fixing-workspace',
  templateUrl: './fixing-workspace.component.html',
  styleUrls: ['./fixing-workspace.component.scss']
})
export class FixingWorkspaceComponent implements OnInit {
  @Input() reportFixModel: any;
  fixWebLinks: any;

  constructor() { }

  ngOnInit() {
    console.log('got things', this.reportFixModel);

    const report = this.reportFixModel.report;
    if (report == null) {
      return;
    }

    const unreachableBooksModel = report.unreachableBooksModel;
    if (unreachableBooksModel && unreachableBooksModel.webLinks) {
      this.fixWebLinks = unreachableBooksModel.webLinks;
    }
  }
}

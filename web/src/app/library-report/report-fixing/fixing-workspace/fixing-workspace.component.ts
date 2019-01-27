import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-fixing-workspace',
  templateUrl: './fixing-workspace.component.html',
  styleUrls: ['./fixing-workspace.component.scss']
})
export class FixingWorkspaceComponent implements OnInit {
  @Input() reportFixModel: any;

  constructor() { }

  ngOnInit() {
  }
}

import { Component, OnInit, Input } from '@angular/core';
import { InfoHostComponent } from '../info-host/info-host.component';

@Component({
  selector: 'app-change-info',
  templateUrl: './change-info.component.html',
  styleUrls: ['./change-info.component.scss']
})
export class ChangeInfoComponent implements InfoHostComponent, OnInit {
  @Input() data: any;

  constructor() { }

  ngOnInit() {
  }

}

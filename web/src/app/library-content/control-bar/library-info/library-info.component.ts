import { Component, Input, OnInit } from '@angular/core';
import { InfoHostComponent } from '../info-host/info-host.component';

@Component({
  selector: 'app-library-info',
  templateUrl: './library-info.component.html',
  styleUrls: ['./library-info.component.scss']
})
export class LibraryInfoComponent implements OnInit, InfoHostComponent {
  @Input() data: any;

  constructor() { }

  ngOnInit() {
  }

}

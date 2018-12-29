import { Component, Input, OnInit } from '@angular/core';
import { IconDefinition } from '@fortawesome/fontawesome-svg-core';
import { faPlus, faMinus, faPen } from '@fortawesome/free-solid-svg-icons';
import { InfoHostComponent } from '../info-host/info-host.component';

@Component({
  selector: 'app-change-info',
  templateUrl: './change-info.component.html',
  styleUrls: ['./change-info.component.scss']
})
export class ChangeInfoComponent implements InfoHostComponent, OnInit {
  faIconAdd: IconDefinition = faPlus;
  faIconDelete: IconDefinition = faMinus;
  faIconChange: IconDefinition = faPen;

  @Input() data: any;

  constructor() { }

  ngOnInit() {
  }

}

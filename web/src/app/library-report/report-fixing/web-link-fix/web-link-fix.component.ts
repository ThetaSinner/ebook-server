import { Component, OnInit, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-web-link-fix',
  templateUrl: './web-link-fix.component.html',
  styleUrls: ['./web-link-fix.component.scss']
})
export class WebLinkFixComponent implements OnInit, OnChanges {
  @Input() fixWebLinks: any;

  constructor() { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.fixWebLinks.currentValue) {
      this.fixWebLinks = changes.fixWebLinks.currentValue;
    }
  }
}

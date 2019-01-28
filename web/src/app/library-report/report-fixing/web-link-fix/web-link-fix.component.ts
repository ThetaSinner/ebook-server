import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-web-link-fix',
  templateUrl: './web-link-fix.component.html',
  styleUrls: ['./web-link-fix.component.scss']
})
export class WebLinkFixComponent implements OnInit {
  @Input() fixWebLinks: any;

  constructor() { }

  ngOnInit() {
    
  }
}

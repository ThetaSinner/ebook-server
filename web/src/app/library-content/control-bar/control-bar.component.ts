import { Component, OnInit } from '@angular/core';
import { faChevronLeft, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { Router } from '@angular/router';
import { routerNgProbeToken } from '@angular/router/src/router_module';

@Component({
  selector: 'app-control-bar',
  templateUrl: './control-bar.component.html',
  styleUrls: ['./control-bar.component.scss']
})
export class ControlBarComponent implements OnInit {
  faChevronLeft: IconDefinition = faChevronLeft;

  constructor(
    private router: Router
  ) { }

  ngOnInit() {
  }

  navigateHome() {
    this.router.navigate(['/libraries']);
  }
}

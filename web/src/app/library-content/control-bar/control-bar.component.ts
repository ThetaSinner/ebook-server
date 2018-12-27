import { Component, Input, OnInit, ViewChild, ComponentFactoryResolver } from '@angular/core';
import { Router } from '@angular/router';
import { faChevronLeft, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { InfoHostItem } from './info-host/info-host-item';
import { InfoHostDirective } from './info-host/info-host.directive';
import { InfoHostComponent } from './info-host/info-host.component';

@Component({
  selector: 'app-control-bar',
  templateUrl: './control-bar.component.html',
  styleUrls: ['./control-bar.component.scss']
})
export class ControlBarComponent implements OnInit {
  @Input() infoHostItems: InfoHostItem[];
  faChevronLeft: IconDefinition = faChevronLeft;

  @ViewChild(InfoHostDirective) infoHost: InfoHostDirective;

  constructor(
    private router: Router,
    private componentFactoryResolver: ComponentFactoryResolver
  ) { }

  ngOnInit() {
    this.loadInfoComponent();
  }

  navigateHome() {
    this.router.navigate(['/libraries']);
  }

  loadInfoComponent() {
    if (this.infoHostItems.length === 0) {
      return;
    }

    const infoItem = this.infoHostItems[0];

    let componentFactory = this.componentFactoryResolver.resolveComponentFactory(infoItem.component);

    let viewContainerRef = this.infoHost.viewContainerRef;
    viewContainerRef.clear();

    let componentRef = viewContainerRef.createComponent(componentFactory);
    (<InfoHostComponent>componentRef.instance).data = infoItem.data;
  }
}

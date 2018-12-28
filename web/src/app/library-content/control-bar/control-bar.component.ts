import { Component, ComponentFactoryResolver, Input, OnInit, ViewChildren, AfterViewInit, QueryList } from '@angular/core';
import { Router } from '@angular/router';
import { faChevronLeft, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { Observable } from 'rxjs';
import { InfoHostItem } from './info-host/info-host-item';
import { InfoHostComponent } from './info-host/info-host.component';
import { InfoHostDirective } from './info-host/info-host.directive';

@Component({
  selector: 'app-control-bar',
  templateUrl: './control-bar.component.html',
  styleUrls: ['./control-bar.component.scss']
})
export class ControlBarComponent implements OnInit, AfterViewInit {
  faChevronLeft: IconDefinition = faChevronLeft;

  @Input() infoHostItems$: Observable<InfoHostItem[]>;
  infoItems: InfoHostItem[];

  @ViewChildren(InfoHostDirective) infoHosts: QueryList<InfoHostDirective>;

  constructor(
    private router: Router,
    private componentFactoryResolver: ComponentFactoryResolver
  ) { }

  ngOnInit() {
    console.log('subscribing for changes to info items');

    this.infoHostItems$.subscribe(value => {
      console.log('got some info items', value);
      this.infoItems = value;
    });
  }

  ngAfterViewInit(): void {
    console.log('after view init, listening for host changes');

    this.infoHosts.changes.subscribe(() => {
      console.log('there were info host changes, loading')
      this.loadInfoComponents();
    });
  }

  navigateHome() {
    this.router.navigate(['/libraries']);
  }

  loadInfoComponents() {
    if (this.infoItems.length === 0) {
      return;
    }

    console.log('loading shit into', this.infoHosts);

    this.infoHosts.forEach((infoHost, index) => {
      const infoItem = this.infoItems[index];

      let componentFactory = this.componentFactoryResolver.resolveComponentFactory(infoItem.component);

      let viewContainerRef = infoHost.viewContainerRef;
      viewContainerRef.clear();

      let componentRef = viewContainerRef.createComponent(componentFactory);
      (<InfoHostComponent>componentRef.instance).data = infoItem.data;
    });
  }
}

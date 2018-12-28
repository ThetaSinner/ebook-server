import { AfterViewInit, ChangeDetectorRef, Component, ComponentFactoryResolver, Input, QueryList, ViewChildren } from '@angular/core';
import { Router } from '@angular/router';
import { faChevronLeft, IconDefinition } from '@fortawesome/free-solid-svg-icons';
import { InfoHostItem } from './info-host/info-host-item';
import { InfoHostComponent } from './info-host/info-host.component';
import { InfoHostDirective } from './info-host/info-host.directive';

@Component({
  selector: 'app-control-bar',
  templateUrl: './control-bar.component.html',
  styleUrls: ['./control-bar.component.scss']
})
export class ControlBarComponent implements AfterViewInit {
  faChevronLeft: IconDefinition = faChevronLeft;

  @Input() infoItems: InfoHostItem[];

  @ViewChildren(InfoHostDirective) infoHosts: QueryList<InfoHostDirective>;

  constructor(
    private router: Router,
    private componentFactoryResolver: ComponentFactoryResolver,
    private changeDetectorRef: ChangeDetectorRef
  ) { }

  ngAfterViewInit(): void {
    this.loadInfoComponents();

    this.infoHosts.changes.subscribe(() => {
      this.loadInfoComponents();
    });
  }

  navigateHome() {
    this.router.navigate(['/libraries']);
  }

  loadInfoComponents() {
    if (!this.infoHosts || !this.infoItems || this.infoItems.length === 0) {
      return;
    }

    this.infoHosts.forEach((infoHost, index) => {
      const infoItem = this.infoItems[index];

      let componentFactory = this.componentFactoryResolver.resolveComponentFactory(infoItem.component);

      let viewContainerRef = infoHost.viewContainerRef;
      viewContainerRef.clear();

      let componentRef = viewContainerRef.createComponent(componentFactory);
      (<InfoHostComponent>componentRef.instance).data = infoItem.data;
    });

    // Changing the infoItems causes the ngFor to re-render the number of outputs.
    // That change must complete for this function to work as it renders to each output slot.
    // You can't change the view while it's mid-render, so kick off a new render cycle because I
    // know it won't cause a loop. Yay all the way home.
    this.changeDetectorRef.detectChanges();
  }
}

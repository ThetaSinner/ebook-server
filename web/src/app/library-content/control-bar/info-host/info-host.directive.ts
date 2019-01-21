import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[appInfoHost]'
})
export class InfoHostDirective {
  constructor(public viewContainerRef: ViewContainerRef) { }
}

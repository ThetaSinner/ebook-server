import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WebLinkFixComponent } from './web-link-fix.component';

describe('WebLinkFixComponent', () => {
  let component: WebLinkFixComponent;
  let fixture: ComponentFixture<WebLinkFixComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WebLinkFixComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WebLinkFixComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

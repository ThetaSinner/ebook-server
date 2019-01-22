import { Component, OnInit } from '@angular/core';
import { IconDefinition } from '@fortawesome/fontawesome-svg-core';
import { faChevronLeft } from '@fortawesome/free-solid-svg-icons';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-library-report-workspace',
  templateUrl: './library-report-workspace.component.html',
  styleUrls: ['./library-report-workspace.component.scss']
})
export class LibraryReportWorkspaceComponent implements OnInit {
  navigateBackIcon: IconDefinition = faChevronLeft;

  constructor(
    private router: Router,
    private activeRoute: ActivatedRoute
  ) { }

  ngOnInit() {
  }

  navigateBackToLibrary() {
    // This is relative to the active route for this component, not the actual url
    // of the child component that's seen
    this.router.navigate(['..'], {relativeTo: this.activeRoute});
  }
}

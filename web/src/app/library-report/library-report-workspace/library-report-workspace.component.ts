import { Component, OnInit } from '@angular/core';
import { IconDefinition } from '@fortawesome/fontawesome-svg-core';
import { faChevronLeft } from '@fortawesome/free-solid-svg-icons';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { switchMap } from 'rxjs/operators';
import { ReportDataService } from '../report-data/report-data.service';

@Component({
  selector: 'app-library-report-workspace',
  templateUrl: './library-report-workspace.component.html',
  styleUrls: ['./library-report-workspace.component.scss']
})
export class LibraryReportWorkspaceComponent implements OnInit {
  navigateBackIcon: IconDefinition = faChevronLeft;
  reportData$: any;

  constructor(
    private router: Router,
    private activedRoute: ActivatedRoute,
    private reportService: ReportDataService
  ) { }

  ngOnInit() {
    this.reportData$ = this.activedRoute.paramMap.pipe(
      switchMap((params: ParamMap) => {
        const libraryName = params.get('libraryName');

        return this.reportService.getReportData(libraryName)
      })
    );
  }

  navigateBackToLibrary() {
    // This is relative to the active route for this component, not the actual url
    // of the child component that's seen
    this.router.navigate(['..'], {relativeTo: this.activedRoute});
  }
}

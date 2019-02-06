import { Component, OnInit } from '@angular/core';
import { IconDefinition } from '@fortawesome/fontawesome-svg-core';
import { faChevronLeft } from '@fortawesome/free-solid-svg-icons';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { switchMap, map } from 'rxjs/operators';
import { ReportDataService } from '../report-data/report-data.service';

@Component({
  selector: 'app-library-report-workspace',
  templateUrl: './library-report-workspace.component.html',
  styleUrls: ['./library-report-workspace.component.scss']
})
export class LibraryReportWorkspaceComponent implements OnInit {
  navigateBackIcon: IconDefinition = faChevronLeft;
  reportData: any;

  metrics: any;
  curationData: any;
  unreachableBooksModel: any;
  missingBooks: any;
  unlistedBooks: any;

  reportFixModel: any;
  libraryName: string;

  constructor(
    private router: Router,
    private activedRoute: ActivatedRoute,
    private reportService: ReportDataService
  ) { 
    this.reportFixModel = {
      report: null,
      fixWebLinks: false,
      fixLocalBooks: false,
      fixMissingBooks: false
    };
  }

  ngOnInit() {
    const sub = this.activedRoute.paramMap.pipe(
      switchMap((params: ParamMap) => {
        const libraryName = params.get('libraryName');
        this.libraryName = libraryName;

        return this.reportService.getReportData(libraryName)
      })
    ).subscribe((val: any) => {
      this.reportData = val;

      const temp = Object.assign({}, this.reportFixModel);
      temp.report = val;
      this.reportFixModel = temp;

      this.metrics = val.metrics;
      this.curationData = val.curationMetrics;
      this.unreachableBooksModel = val.unreachableBooksModel;
      this.missingBooks = val.missingBooks;
      this.unlistedBooks = val.unlistedBooks;

      sub.unsubscribe();
    });
  }

  navigateBackToLibrary() {
    // This is relative to the active route for this component, not the actual url
    // of the child component that's seen
    this.router.navigate(['..'], {relativeTo: this.activedRoute});
  }

  toggleWebLinkFixes() {
    this.reportFixModel.fixWebLinks = !this.reportFixModel.fixWebLinks;
  }

  toggleLocalBookFixes() {
    this.reportFixModel.fixLocalBooks = !this.reportFixModel.fixLocalBooks;
  }

  toggleMissingBookFixes() {
    this.reportFixModel.fixMissingBooks = !this.reportFixModel.fixMissingBooks;
  }
}

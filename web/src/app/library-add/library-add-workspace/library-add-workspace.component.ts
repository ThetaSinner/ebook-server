import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { IconDefinition } from '@fortawesome/fontawesome-svg-core';
import { faBook, faLink, faUpload, faChevronLeft } from '@fortawesome/free-solid-svg-icons';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-library-add-workspace',
  templateUrl: './library-add-workspace.component.html',
  styleUrls: ['./library-add-workspace.component.scss']
})
export class LibraryAddWorkspaceComponent implements OnInit {
  addBookIcon: IconDefinition = faBook;
  uploadBookIcon: IconDefinition = faUpload;
  addLinkIcon: IconDefinition = faLink;
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

  navigateToAddRemoteBook() {
    this.router.navigate(['remote'], {relativeTo: this.activeRoute});
  }

  navigateToUploadBook() {
    this.router.navigate(['upload'], {relativeTo: this.activeRoute});
  }

  navigateToAddBookLink() {
    this.router.navigate(['link'], {relativeTo: this.activeRoute});
  }
}

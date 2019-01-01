import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { IconDefinition } from '@fortawesome/fontawesome-svg-core';
import { faBook, faUpload, faLink } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-library-add-workspace',
  templateUrl: './library-add-workspace.component.html',
  styleUrls: ['./library-add-workspace.component.scss']
})
export class LibraryAddWorkspaceComponent implements OnInit {
  addBookIcon: IconDefinition = faBook;
  uploadBookIcon: IconDefinition = faUpload;
  addLinkIcon: IconDefinition = faLink;

  constructor(
    private router: Router,
    private activeRoute: ActivatedRoute
  ) { }

  ngOnInit() {
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

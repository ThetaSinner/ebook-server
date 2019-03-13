import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {IconDefinition} from '@fortawesome/fontawesome-svg-core';
import {faBook, faChevronLeft, faLink, faUpload} from '@fortawesome/free-solid-svg-icons';

enum MediaType {
  Books,
  Videos
}

const mediaTypeNames = {
  [MediaType.Books]: "Books",
  [MediaType.Videos]: "Videos"
};

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

  private _selectedMediaType: MediaType = MediaType.Books;

  set selectedMediaType(val: string) {
    let mediaType = Object.keys(mediaTypeNames).find(key => mediaTypeNames[key] === val);
    this._selectedMediaType = parseInt(mediaType);
  }

  get selectedMediaType() {
    return mediaTypeNames[this._selectedMediaType]
  }

  get mediaTypes(): string[] {
    return Object.keys(mediaTypeNames).map(key => mediaTypeNames[key])
  }

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

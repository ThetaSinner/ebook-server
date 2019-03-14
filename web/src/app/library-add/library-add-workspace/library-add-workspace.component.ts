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
  addIcon: IconDefinition = faBook;
  uploadIcon: IconDefinition = faUpload;
  addLinkIcon: IconDefinition = faLink;
  navigateBackIcon: IconDefinition = faChevronLeft;

  private _selectedMediaType: MediaType = MediaType.Books;
  private _selectedAddMethod: string = 'upload';

  set selectedMediaType(val: string) {
    let mediaType = Object.keys(mediaTypeNames).find(key => mediaTypeNames[key] === val);
    this._selectedMediaType = parseInt(mediaType);

    this.applyRouting();
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

  selectAddRemoteMedia() {
    this._selectedAddMethod = 'remote';
    this.applyRouting();
  }

  selectUploadMedia() {
    this._selectedAddMethod = 'upload';
    this.applyRouting();
  }

  selectAddMediaLink() {
    this._selectedAddMethod = 'link';
    this.applyRouting();
  }

  private applyRouting() {
    let routeBase = {
      [MediaType.Books]: "book",
      [MediaType.Videos]: "video"
    }[this._selectedMediaType];

    this.router.navigate([routeBase, this._selectedAddMethod], {relativeTo: this.activeRoute}).then(succeeded => {
      if (!succeeded) {
        console.error('Routing failed, try again?');
      }
    }).catch(e => {
      console.error('Failure while attempting to apply routing', e);
    });
  }
}

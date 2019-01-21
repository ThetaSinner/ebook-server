import { Component, OnInit } from '@angular/core';
import { LibraryDataService } from '../library-data/library-data.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-library-workspace',
  templateUrl: './library-workspace.component.html',
  styleUrls: ['./library-workspace.component.scss']
})
export class LibraryWorkspaceComponent implements OnInit {
  libraryData: any;

  constructor(
    private libraryDataService: LibraryDataService,
    private router: Router
  ) { }

  ngOnInit() {
    this.getLibraries();
  }

  createLibrary(libraryName: string) {
    const component = this;
    const subscription = this.libraryDataService.createLibrary(libraryName).subscribe(_ => {
      component.getLibraries();
      subscription.unsubscribe();
    });
  }

  private getLibraries() {
    const component = this;
    const subscription = this.libraryDataService.getLibraries().subscribe(libraryData => {
      component.libraryData = libraryData;
      subscription.unsubscribe();
    });
  }

  selectLibrary(libraryName: string) {
    this.router.navigate(['/content', libraryName]);
  }
}

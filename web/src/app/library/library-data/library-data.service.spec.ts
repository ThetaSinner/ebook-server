import { TestBed } from '@angular/core/testing';

import { LibraryDataService } from './library-data.service';

describe('LibraryDataService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: LibraryDataService = TestBed.get(LibraryDataService);
    expect(service).toBeTruthy();
  });
});

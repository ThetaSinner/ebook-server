import { TestBed } from '@angular/core/testing';

import { LibraryChangeService } from './library-change.service';

describe('LibraryChangeService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: LibraryChangeService = TestBed.get(LibraryChangeService);
    expect(service).toBeTruthy();
  });
});

import { TestBed } from '@angular/core/testing';

import { InfoHostDataSourceService } from './info-host-data-source.service';

describe('InfoHostDataSourceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: InfoHostDataSourceService = TestBed.get(InfoHostDataSourceService);
    expect(service).toBeTruthy();
  });
});

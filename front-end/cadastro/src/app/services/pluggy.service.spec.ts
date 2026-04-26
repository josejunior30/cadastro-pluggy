import { TestBed } from '@angular/core/testing';

import { PluggyService } from './pluggy.service';

describe('PluggyService', () => {
  let service: PluggyService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PluggyService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

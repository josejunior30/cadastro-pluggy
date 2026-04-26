import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PluggyConnect } from './pluggy-connect';

describe('PluggyConnect', () => {
  let component: PluggyConnect;
  let fixture: ComponentFixture<PluggyConnect>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PluggyConnect],
    }).compileComponents();

    fixture = TestBed.createComponent(PluggyConnect);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

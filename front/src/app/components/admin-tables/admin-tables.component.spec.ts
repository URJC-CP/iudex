import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminTablesComponent } from './admin-tables.component';

describe('AdminTablesComponent', () => {
  let component: AdminTablesComponent;
  let fixture: ComponentFixture<AdminTablesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminTablesComponent]
    });
    fixture = TestBed.createComponent(AdminTablesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

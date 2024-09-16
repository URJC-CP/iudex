import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JudgeAdminTablesComponent } from './judge-admin-tables.component';

describe('JudgeAdminTablesComponent', () => {
  let component: JudgeAdminTablesComponent;
  let fixture: ComponentFixture<JudgeAdminTablesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [JudgeAdminTablesComponent]
    });
    fixture = TestBed.createComponent(JudgeAdminTablesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

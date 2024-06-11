import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentProblemsComponent } from './student-problems.component';

describe('StudentProblemsComponent', () => {
  let component: StudentProblemsComponent;
  let fixture: ComponentFixture<StudentProblemsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StudentProblemsComponent]
    });
    fixture = TestBed.createComponent(StudentProblemsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

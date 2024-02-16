import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentRankingComponent } from './student-ranking.component';

describe('StudentRankingComponent', () => {
  let component: StudentRankingComponent;
  let fixture: ComponentFixture<StudentRankingComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StudentRankingComponent]
    });
    fixture = TestBed.createComponent(StudentRankingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ContestDTO } from 'src/app/dto/contest.dto';
import { ProblemDTO } from 'src/app/dto/problem.dto';
import { ContestService } from 'src/app/services/contest.service';
import { ProblemService } from 'src/app/services/problem.service';

@Component({
  selector: 'app-student-problems',
  templateUrl: './student-problems.component.html',
  styleUrls: ['../../../styles.css']
})
export class StudentProblemsComponent {

  id: string | undefined;
  contest: ContestDTO | undefined;
  problemList: ProblemDTO[] = [];

  constructor(private activatedRouter: ActivatedRoute, private contestService: ContestService, private problemService: ProblemService) {
    activatedRouter.url.subscribe((data) => {
      this.id = data[2].path;
    });
  }

  ngOnInit() {
    this.contestService.getSelectedContest(this.id!).subscribe((data) => {
      this.contest = data;
      for (let i = 0; i < data.listaProblemas.length; i++) {
        this.problemService.getSelectedProblem(String(data.listaProblemas[i].id!)).subscribe((problem) => {
          this.problemList?.push(problem);
        });
      }
    });
  }

  pdf(problemId: number) {
    this.problemService.getPdfFromProblem(String(problemId));
  }

  testCases(problemId: number) {

  }
}

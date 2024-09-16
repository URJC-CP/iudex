import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';
import { ContestDTO } from 'src/app/dto/contest.dto';
import { ProblemDTO } from 'src/app/dto/problem.dto';
import { ContestService } from 'src/app/services/contest.service';
import { ProblemService } from 'src/app/services/problem.service';

@Component({
  selector: 'app-student-problems',
  templateUrl: './student-problems.component.html',
  providers: [MessageService]
})
export class StudentProblemsComponent {

  id: string | undefined;
  contest: ContestDTO | undefined;
  problemList: ProblemDTO[] = [];
  visible: { [id: string]: boolean; } = {};

  constructor(private activatedRouter: ActivatedRoute, private contestService: ContestService, private problemService: ProblemService, private messageService: MessageService, public translate: TranslateService) {
    activatedRouter.url.subscribe((data) => {
      this.id = data[2].path;
    });
  }

  ngOnInit() {
    this.contestService.getSelectedContest(this.id!).subscribe((data) => {
      this.contest = data;
      for (let i = 0; i < data.listaProblemas.length; i++) {
        this.problemList?.push(data.listaProblemas[i]);
        this.visible[data.listaProblemas[i].id!] = false;
      }
    });
  }

  pdf(problemId: number) {
    this.problemService.getPdfFromProblem(String(problemId));
  }

  testCases(problemId: number) {
    this.visible[problemId] = !this.visible[problemId];
  }

  messageIn() {
    this.messageService.add({ key: 'tl', severity: 'success', summary: this.translate.instant("CopiedClipboard"), detail: this.translate.instant("InputCopiedCorrectly") });
  }

  messageOut() {
    this.messageService.add({ key: 'tl', severity: 'success', summary: this.translate.instant("CopiedClipboard"), detail: this.translate.instant("OutputCopiedCorrectly") });
  }
}

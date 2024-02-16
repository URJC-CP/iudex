import { Component, Input } from '@angular/core';
import { ActivatedRoute, Data, Router } from '@angular/router';
import { ContestDTO } from 'src/app/dto/contest.dto';
import { LanguageDTO } from 'src/app/dto/language.dto';
import { ProblemDTO } from 'src/app/dto/problem.dto';
import { ContestService } from 'src/app/services/contest.service';
import { ProblemService } from 'src/app/services/problem.service';

interface ProblemName {
  name: string;
}

@Component({
  selector: 'app-submit-button',
  templateUrl: './submit-button.component.html'
})
export class SubmitButtonComponent {

  @Input() contestId: string;
  @Input() selectedProblem: string;
  visible: boolean = false;
  position: string = 'top';
  contest: ContestDTO | undefined;
  problems: ProblemName[] = [];
  selProblem: ProblemName;
  lang: LanguageDTO[] | undefined;
  selectedLang: LanguageDTO | undefined;
  loaded: boolean = true;

  constructor(private contestService: ContestService, private problemService: ProblemService) {
  }

  ngOnInit() {
    this.selProblem = { name: this.selectedProblem };
    this.loaded = false;
    this.contestService.getSelectedContest(this.contestId).subscribe((data) => {
      this.contest = data;
      for (let i = 0; i < data.listaProblemas.length; i++) {
        this.problemService.getSelectedProblem(String(data.listaProblemas[i].id!)).subscribe((problem) => {
          this.problems = [...this.problems, { name: problem.nombreEjercicio }];
          //.push({name: problem.nombreEjercicio});
          if (i == data.listaProblemas.length - 1) {
            this.loaded = true;
          }

        });
      }
    });
  }

  showDialog() {
    this.visible = true;
  }

  submitCode() {
    this.visible = false;
  }

  close(){
    this.selProblem = {name: 'Select a problem'};
  }

}

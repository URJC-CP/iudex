import { Component, Input } from '@angular/core';
import { MessageService } from 'primeng/api';
import { FileSelectEvent } from 'primeng/fileupload';
import { ContestDTO } from 'src/app/dto/contest.dto';
import { SubmissionDTO } from 'src/app/dto/submission.dto';
import { ContestService } from 'src/app/services/contest.service';
import { LanguageService } from 'src/app/services/language.service';
import { ProblemService } from 'src/app/services/problem.service';
import { SubmissionService } from 'src/app/services/submission.service';

interface ProblemName {
  name: string;
  id: string;
}
interface LanguageName {
  name: string;
  id: string;
}

@Component({
  selector: 'app-submit-button',
  templateUrl: './submit-button.component.html',
  providers: [MessageService]
})
export class SubmitButtonComponent {

  @Input() contestId: string;
  @Input() selectedProblem: string | undefined;
  @Input() problemId: string;
  visible: boolean = false;
  position: string = 'top';
  contest: ContestDTO | undefined;
  problems: ProblemName[] = [];
  selProblem: ProblemName | undefined;
  lang: LanguageName[] = [];
  selectedLang: LanguageName | undefined;
  loaded: boolean = true;
  uploadedFile: File | undefined;
  isDisabled: boolean = false;

  constructor(private contestService: ContestService, private problemService: ProblemService, private langSevice: LanguageService, private submissionService: SubmissionService, private messageService: MessageService) {
  }

  ngOnInit() {
    this.uploadedFile = undefined;
    this.loadProblems();
    this.loaded = false;
    this.langSevice.getAllLanguages().subscribe((data) => {
      for (let i = 0; i < data.length; i++) {
        this.lang = [...this.lang, { name: data[i].nombreLenguaje!, id: String(data[i].id!) }];
      }
    });
    this.contestService.getSelectedContest(this.contestId).subscribe((data) => {
      this.contest = data;
      for (let i = 0; i < data.listaProblemas.length; i++) {
        this.problems = [...this.problems, { name: data.listaProblemas[i].nombreEjercicio, id: String(data.listaProblemas[i].id) }];
        //.push({name: problem.nombreEjercicio});
        if (i == data.listaProblemas.length - 1) {
          this.loaded = true;
        }
      }
    });
  }

  loadProblems() {
    if (this.selectedProblem) {
      this.selProblem = { name: this.selectedProblem, id: this.problemId };
      this.selectedProblem = undefined;
    }
  }

  onFileSelected(event: FileSelectEvent) {
    this.uploadedFile = event.files[0];
  }

  showDialog() {
    this.visible = true;
    this.loadProblems()
  }

  submitCode() {
    if (this.selProblem && this.selectedLang && this.uploadedFile) {
      this.visible = false;
      //this.submissionService.createSubmission(this.uploadedFile, this.contestId, this.selectedLang.id, this.selProblem.id, teamId);
    } else {
      this.visible = true;
      this.messageService.add({ key: 'tl', severity: 'error', summary: $localize`Required fields`, detail: $localize`You must complete the fields to make the submission` });
    }

  }

}

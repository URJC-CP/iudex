import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { SubmissionDTO } from 'src/app/dto/submission.dto';
import { TeamScoreDTO } from 'src/app/dto/teamScore.dto';
import { ContestService } from 'src/app/services/contest.service';
import { LanguageService } from 'src/app/services/language.service';
import { SubmissionService } from 'src/app/services/submission.service';
import { UserService } from 'src/app/services/user.service';

interface Column {
  field: string;
  header: string;
}

interface Submission {
  timestamp: string;
  problem: string;
  language: string;
  result: string;
}

interface LanguageName {
  name: string;
  id: string;
}

interface Theme {
  name: string;
}

interface ProblemName {
  name: string;
  id: string;
}

interface Column {
  field: string;
  header: string;
}

interface problems {
  score: string;
  tries: string;
  first: boolean;
  color: string
}

interface scores {
  position: string;
  team: string;
  score: string;
  problems: Map<String, problems>
}

@Component({
  selector: 'app-student-home',
  templateUrl: './student-home.component.html',
  providers: [MessageService]
})
export class StudentHomeComponent {

  submissions: Submission[] = [];
  loaded: boolean;
  contestId!: string;
  editorOptions = { theme: 'vs-dark', language: '' };
  code: string = '';
  lang: LanguageName[] = [];
  selectedLanguage: LanguageName | undefined;
  selectedTheme: Theme = { name: 'vs-dark' };
  themes: Theme[] = [{ name: 'vs' }, { name: 'vs-dark' }, { name: 'hc-black' }];
  problems: ProblemName[] = [];
  selProblem: ProblemName | undefined;
  teamId: string = '1';
  userId: string;

  problemScore!: TeamScoreDTO[];
  scoreData: scores[] = [];
  cols: Column[] = [];
  end: number;

  constructor(private submissionService: SubmissionService, private activatedRouter: ActivatedRoute, private langSevice: LanguageService,
    private contestService: ContestService, private messageService: MessageService, private userService: UserService) {
    activatedRouter.url.subscribe((data) => {
      this.contestId = data[2].path;
    });
  }

  ngOnInit() {
    this.loadScoreboard();
    this.loaded = false;
    this.userService.getCurrentUser().subscribe((data) => {
      this.userId = String(data.id!);
      this.contestService.getCurrentTeam(this.contestId, this.userId).subscribe((data) => {
        this.teamId = String(data.id!);
        this.contestService.getSubmissionsByContestAndTeam(this.contestId, this.teamId).subscribe((data) => {
          for (let i = 0; i < data.length; i++) {
            let time = new Date(data[i].timestamp);
            let result = "";
            if (data[i].resultado == "accepted") { result = "AC" }
            else if (data[i].resultado == "wrong_answer") { result = "WA" }
            else if (data[i].resultado == "time_limit_exceeded") { result = "TLE" }
            else if (data[i].resultado.startsWith("run_time_error")) { result = "RTE" }
            else if (data[i].resultado.startsWith("FAILED IN COMPILER")) { result = "CE" }
            this.submissions[this.submissions.length] = ({
              timestamp: time.toLocaleString().replace(", ", " "),
              problem: data[i].problem.nombreEjercicio,
              language: data[i].language.nombreLenguaje,
              result: result
            });
            if (i == data.length - 1) {
              this.loaded = true;
            }
          }
        })
      });
    });

    this.contestService.getSelectedContest(this.contestId).subscribe((data) => {
      for (let i = 0; i < data.listaProblemas.length; i++) {
        this.problems = [...this.problems, { name: data.listaProblemas[i].nombreEjercicio, id: String(data.listaProblemas[i].id) }];
      }
      for (let i = 0; i < data.lenguajesAceptados.length; i++) {
        this.lang = [...this.lang, { name: data.lenguajesAceptados[i].nombreLenguaje!, id: String(data.lenguajesAceptados[i].id!) }];
      }
      this.end = data.listaProblemas.length;
      for (let i = 0; i < data.listaProblemas.length; i++) {
        this.cols.push({ field: String(data.listaProblemas[i].id), header: data.listaProblemas[i].nombreEjercicio });
      }
    });

  }

  getSeverity(result: string) {
    if (result == "AC") {
      return 'success';
    }
    else {
      return 'error';
    }
  }

  selectOptions() {
    if (this.selectedLanguage?.name == 'python3') {
      this.editorOptions = { theme: this.selectedTheme.name, language: 'python' };
    } else {
      this.editorOptions = { theme: this.selectedTheme.name, language: this.selectedLanguage?.name! };
    }
  }

  submitCode() {
    if (this.selProblem && this.selectedLanguage && this.code.length > 0) {
      let ext = '';
      switch (this.selectedLanguage.name) {
        case 'java':
          ext = 'java';
          break;
        case 'python3':
          ext = 'py';
          break;
        case 'c':
          ext = 'c';
          break;
        case 'cpp':
          ext = 'cpp';
          break;
        case 'sql':
          ext = 'sql';
          break;
      }
      var file = new File([this.code], "submission." + ext);
      this.submissionService.createSubmission(file, this.contestId, this.selectedLanguage.id, this.selProblem.id, this.teamId).subscribe();
      this.messageService.add({ key: 'tl', severity: 'success', summary: $localize`Submission sent!!`, detail: $localize`Your submission has been sent correctly` });
    } else {
      this.messageService.add({ key: 'tl', severity: 'error', summary: $localize`Required fields`, detail: $localize`You must complete the fields to make the submission` });
    }

  }

  loadScoreboard(){
    this.loaded = false;

    this.contestService.getTeamScoreboard(this.contestId, this.teamId).subscribe((data) => {
      this.problemScore = data;
      for (let i = 0; i < data.length; i++) {
        let map = new Map<String, problems>();
        data[i].scoreList.forEach((scoreProblem) => {
          let color = ""
          if (scoreProblem.first) {
            color = 'dark green'
          } else if (scoreProblem.solved) {
            color = 'green'
          } else {
            color = 'red'
          }
          map.set(String(scoreProblem.problem.id), { score: String(scoreProblem.score), tries: String(scoreProblem.tries), first: scoreProblem.first, color: color })
        });
        this.scoreData.push({ position: String(i + 1), team: data[i].team?.nombreEquipo, score: String(data[i].score), problems: map })
        if (i == data.length - 1) {
          this.loaded = true;
        }
      }
    });
  }

}

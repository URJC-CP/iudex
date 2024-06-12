import { Component } from '@angular/core';
import { ActivatedRoute, NavigationSkipped, NavigationStart, Router } from '@angular/router';
import { ContestService } from 'src/app/services/contest.service';
import { ProblemService } from 'src/app/services/problem.service';
import { SubmissionService } from 'src/app/services/submission.service';
import { ConfirmationService } from 'primeng/api';


interface Column {
  header: string;
  field: string;
}

interface Data {
  id: string;
}

interface DataContest extends Data {
  name: string;
  desc: string;
  owner: string;
  start: string;
  end: string;
}

interface DataProblem extends Data {
  name: string;
  timeLim: string;
  memLim: string;
  cases: string;
  contests: string;
  submissions: string;
}

interface DataSubmission extends Data {
  time: string;
  team: string;
  problem: string;
  lang: string;
  result: string;
}

@Component({
  selector: 'app-admin-tables',
  templateUrl: './admin-tables.component.html',
  providers: [ConfirmationService]
})

export class AdminTablesComponent {

  type: string;
  title: string;
  loaded: boolean = false;
  cols: Column[] = [];
  data: Data[];
  contestData: DataContest[] = [];
  problemData: DataProblem[] = [];
  submissionData: DataSubmission[] = [];

  constructor(private activatedRouter: ActivatedRoute, private router: Router, private contestService: ContestService, private problemService: ProblemService, private submissionService: SubmissionService, private confirmationService: ConfirmationService) {
    this.router.routeReuseStrategy.shouldReuseRoute = function () {
      return false;
    };

    activatedRouter.url.subscribe((data) => {
      if (data[1].path == "contest") {
        this.type = "contest";
      } else if (data[1].path == "problem") {
        this.type = "problem";
      } else if (data[1].path == "submission") {
        this.type = "submission";
      }
    });
  }

  ngOnInit() {
    if (this.type == "contest") {
      this.contestTable();
      console.log(this.type);

    }
    else if (this.type == "problem") {
      this.problemTable();
    }
    else if (this.type == "submission") {
      this.submissionTable();
    }
  }

  contestTable() {
    this.title = $localize`Contests`;
    this.cols.push({ header: $localize`Id`, field: 'id' }, { header: $localize`Name`, field: 'name' }, { header: $localize`Description`, field: 'desc' }, { header: $localize`Owner`, field: 'owner' }, { header: $localize`Start`, field: 'start' }, { header: $localize`End`, field: 'end' });
    this.contestService.getAllContests().subscribe((data) => {
      for (let i = 0; i < data.length; i++) {
        var start = new Date(data[i].startDateTime).toLocaleString();
        var end = new Date(data[i].endDateTime).toLocaleString();
        this.contestData.push({ id: data[i].id, name: data[i].nombreContest, desc: data[i].descripcion, owner: data[i].teamPropietario.nombreEquipo, start: start, end: end });
      }
      this.loaded = true;
    });
    this.data = this.contestData;
  }

  problemTable() {
    this.title = $localize`Problems`;
    this.cols.push({ header: $localize`Id`, field: 'id' }, { header: $localize`Name`, field: 'name' }, { header: $localize`Time Limit`, field: 'timeLim' }, { header: $localize`Memory Limit`, field: 'memLim' }, { header: $localize`Cases #`, field: 'cases' }, { header: $localize`Contests #`, field: 'contests' }, { header: $localize`Submissions #`, field: 'submissions' });
    this.problemService.getAllProblems().subscribe((data) => {
      for (let i = 0; i < data.length; i++) {
        this.problemData.push({ id: data[i].id.toString(), name: data[i].nombreEjercicio, timeLim: data[i].timeout, memLim: data[i].memoryLimit, cases: data[i].samples.length.toString(), contests: data[i].numContest.toString(), submissions: data[i].submissions.length.toString() });
      }
      this.loaded = true;
    });
    this.data = this.problemData;
  }

  submissionTable() {
    this.title = $localize`Submissions`;
    this.cols.push({ header: $localize`Id`, field: 'id' }, { header: $localize`Time`, field: 'time' }, { header: $localize`Team`, field: 'time' }, { header: $localize`Problem`, field: 'problem' }, { header: $localize`Language`, field: 'lang' }, { header: $localize`Result`, field: 'result' });
    this.submissionService.getAllSubmissions("", "").subscribe((data) => {
      for (let i = 0; i < data.length; i++) {
        var time = new Date(data[i].timestamp).toLocaleString();
        this.submissionData.push({ id: data[i].id.toString(), time: time, team: data[i].team.nombreEquipo, problem: data[i].problem.nombreEjercicio, lang: data[i].language.nombreLenguaje, result: data[i].resultado });
      }
      this.loaded = true;
    });
    this.data = this.submissionData;
  }

  delete(id: string) {
    // traducir type
    if (this.type === "contest") {
      this.confirmationService.confirm({
        message: $localize`Are you sure you want to delete this ` + this.type + '?',
        header: $localize`Confirmation`,
        icon: 'pi pi-exclamation-triangle',
        rejectButtonStyleClass: "p-button-outlined",
        accept: () => {
          this.contestService.deleteContest(id).subscribe();
        }
      });
      this.contestData = this.contestData.filter((data) => data.id !== id);
      this.data = this.contestData;
    }
    else if (this.type === "problem") {
      this.confirmationService.confirm({
        message: $localize`Are you sure you want to delete this ` + this.type + '?',
        header: $localize`Confirmation`,
        icon: 'pi pi-exclamation-triangle',
        rejectButtonStyleClass: "p-button-outlined",
        accept: () => {
          this.problemService.deleteProblem(id).subscribe();
        }
      });
      this.problemData = this.problemData.filter((data) => data.id !== id);
      this.data = this.problemData;
    }
    else if (this.type === "submission") {
      this.confirmationService.confirm({
        message: $localize`Are you sure you want to delete this ` + this.type + '?',
        header: $localize`Confirmation`,
        icon: 'pi pi-exclamation-triangle',
        rejectButtonStyleClass: "p-button-outlined",
        accept: () => {
          this.submissionService.deleteSubmission(id).subscribe();
        }
      });
      this.submissionData = this.submissionData.filter((data) => data.id !== id);
      this.data = this.submissionData;
    }
  }

}

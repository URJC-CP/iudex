import { Component } from '@angular/core';
import { ActivatedRoute, NavigationSkipped, NavigationStart, Router } from '@angular/router';
import { ContestService } from 'src/app/services/contest.service';
import { ProblemService } from 'src/app/services/problem.service';
import { SubmissionService } from 'src/app/services/submission.service';
import { ConfirmationService } from 'primeng/api';
import { UserService } from 'src/app/services/user.service';
import { AdminService } from 'src/app/services/admin.service';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { Subscription, firstValueFrom } from 'rxjs';


interface Column {
  header: string;
  field: string;
}

interface Data {
  id: number;
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

interface DataUser extends Data {
  username: string;
  name: string;
  roles: string;
}

// interface DataResult extends Data {
//   time: string;
//   submission: string;
//   problem: string;
//   lang: string;
//   result: string;
// }

// Temporal, backend no devuelve submission
interface DataResult extends Data {
  time: string;
  lang: string;
  result: string;
}

@Component({
  selector: 'app-admin-tables',
  templateUrl: './judge-admin-tables.component.html',
  providers: [ConfirmationService]
})

export class JudgeAdminTablesComponent {

  type: string;
  title: string;
  loaded: boolean = false;
  cols: Column[] = [];
  data: Data[];
  contestData: DataContest[] = [];
  problemData: DataProblem[] = [];
  submissionData: DataSubmission[] = [];
  userData: DataUser[] = [];
  resultData: DataResult[] = [];
  private langChangeSub: Subscription;

  constructor(private activatedRouter: ActivatedRoute, private router: Router, private contestService: ContestService, private problemService: ProblemService, private submissionService: SubmissionService, private confirmationService: ConfirmationService, private userService: UserService, private adminService: AdminService, public translate: TranslateService) {
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
      } else if (data[1].path == "user") {
        this.type = "user";
      } else if (data[1].path == "result") {
        this.type = "result";
      }
    });
  }

  async ngOnInit() {
    const translation = await firstValueFrom(this.translate.get('Name'));
    this.langChangeSub = this.translate.onLangChange.subscribe((event: LangChangeEvent) => {
      this.tablesInit();
    });
    this.tablesInit();
  }

  tablesInit() {
    if (this.type == "contest") {
      this.contestTable();
    }
    else if (this.type == "problem") {
      this.problemTable();
    }
    else if (this.type == "submission") {
      this.submissionTable();
    }
    else if (this.type == "user") {
      this.userTable();
    }
    else if (this.type == "result") {
      this.resultTable();
    }
  }

  contestTable() {
    this.cols = [];
    this.contestData = [];
    this.title = this.translate.instant("Contests");
    this.cols.push({ header: this.translate.instant("Id"), field: 'id' }, { header: this.translate.instant("Name"), field: 'name' }, { header: this.translate.instant("Description"), field: 'desc' }, { header: this.translate.instant("Owner"), field: 'owner' }, { header: this.translate.instant("Start"), field: 'start' }, { header: this.translate.instant("End"), field: 'end' });
    this.contestService.getAllContests().subscribe((data) => {
      for (let i = 0; i < data.length; i++) {
        var start = new Date(data[i].startDateTime).toLocaleString();
        var end = new Date(data[i].endDateTime).toLocaleString();
        this.contestData.push({ id: +data[i].id, name: data[i].nombreContest, desc: data[i].descripcion, owner: data[i].teamPropietario.nombreEquipo, start: start, end: end });
      }
      this.loaded = true;
    });
    this.data = this.contestData;
  }

  problemTable() {
    this.cols = [];
    this.problemData = [];
    this.title = this.translate.instant("Problems");
    this.cols.push({ header: this.translate.instant("Id"), field: 'id' }, { header: this.translate.instant("Name"), field: 'name' }, { header: this.translate.instant("TL"), field: 'timeLim' }, { header: this.translate.instant("ML"), field: 'memLim' }, { header: this.translate.instant("CasesNum"), field: 'cases' }, { header: this.translate.instant("ContestsNum"), field: 'contests' }, { header: this.translate.instant("SubmissionsNum"), field: 'submissions' });
    this.problemService.getAllProblems().subscribe((data) => {
      for (let i = 0; i < data.length; i++) {
        this.problemData.push({ id: data[i].id, name: data[i].nombreEjercicio, timeLim: data[i].timeout, memLim: data[i].memoryLimit, cases: data[i].samples.length.toString(), contests: data[i].numContest.toString(), submissions: data[i].submissions.length.toString() });
      }
      this.loaded = true;
    });
    this.data = this.problemData;
  }

  submissionTable() {
    this.cols = [];
    this.submissionData = [];
    this.title = this.translate.instant("Submissions");
    this.cols.push({ header: this.translate.instant("Id"), field: 'id' }, { header: this.translate.instant("Time"), field: 'time' }, { header: this.translate.instant("Team"), field: 'team' }, { header: this.translate.instant("Problem"), field: 'problem' }, { header: this.translate.instant("Language"), field: 'lang' }, { header: this.translate.instant("Result"), field: 'result' });
    this.submissionService.getAllSubmissions("", "").subscribe((data) => {
      for (let i = 0; i < data.length; i++) {
        var time = new Date(data[i].timestamp).toLocaleString();
        this.submissionData.push({ id: data[i].id, time: time, team: data[i].team.nombreEquipo, problem: data[i].problem.nombreEjercicio, lang: data[i].language.nombreLenguaje, result: data[i].resultado });
      }
      this.loaded = true;
    });
    this.data = this.submissionData;
  }

  userTable() {
    this.cols = [];
    this.userData = [];
    this.title = this.translate.instant("Users");
    this.cols.push({ header: this.translate.instant("Id"), field: 'id' }, { header: this.translate.instant("Username"), field: 'username' }, { header: this.translate.instant("Name"), field: 'name' }, { header: this.translate.instant("Roles"), field: 'roles' });
    this.userService.getAllUsers().subscribe((data) => {
      for (let i = 0; i < data.length; i++) {
        this.userData.push({ id: data[i].id, username: data[i].nickname, name: data[i].name, roles: data[i].rolesString });
      }
      this.loaded = true;
    });
    this.data = this.userData;
  }

  resultTable() {
    this.cols = [];
    this.resultData = [];
    this.title = this.translate.instant("Results");
    this.cols.push({ header: this.translate.instant("Id"), field: 'id' }, { header: this.translate.instant("Time"), field: 'time' }, { header: this.translate.instant("Submission"), field: 'submission' }, { header: this.translate.instant("Problem"), field: 'problem' }, { header: this.translate.instant("Language"), field: 'lang' }, { header: this.translate.instant("Result"), field: 'result' });
    this.adminService.getAllResults().subscribe((data) => {
      for (let i = 0; i < data.length; i++) {
        var time = new Date(data[i].timestamp).toLocaleString();
        // this.resultData.push({ id: data[i].id.toString(), time: time, submission: data[i].submission.id.toString(), problem: data[i].submission.problem.id.toString(), lang: data[i].language.nombreLenguaje, result: data[i].resultadoRevision });
        this.resultData.push({ id: data[i].id, time: time, lang: data[i].language.nombreLenguaje, result: data[i].resultadoRevision });
      }
      this.loaded = true;
    });
    this.data = this.resultData;
  }

  delete(id: number) {
    if (this.type === "contest") {
      this.confirmationService.confirm({
        message: this.translate.instant("deleteConf") + this.title.toLowerCase().slice(0, -1) + '?',
        header: this.translate.instant("Confirmation"),
        icon: 'pi pi-exclamation-triangle',
        rejectButtonStyleClass: "p-button-outlined",
        accept: () => {
          this.contestService.deleteContest(id.toString()).subscribe();
          this.contestData = this.contestData.filter((data) => data.id !== id);
        }
      });
      this.data = this.contestData;
    }
    else if (this.type === "problem") {
      this.confirmationService.confirm({
        message: this.translate.instant("deleteConf") + this.title.toLowerCase().slice(0, -1) + '?',
        header: this.translate.instant("Confirmation"),
        icon: 'pi pi-exclamation-triangle',
        rejectButtonStyleClass: "p-button-outlined",
        accept: () => {
          this.problemService.deleteProblem(id.toString()).subscribe();
          this.problemData = this.problemData.filter((data) => data.id !== id);
        }
      });
      this.data = this.problemData;
    }
    else if (this.type === "submission") {
      this.confirmationService.confirm({
        message: this.translate.instant("deleteConf") + this.title.toLowerCase().slice(0, -1) + '?',
        header: this.translate.instant("Confirmation"),
        icon: 'pi pi-exclamation-triangle',
        rejectButtonStyleClass: "p-button-outlined",
        accept: () => {
          this.submissionService.deleteSubmission(id.toString()).subscribe();
          this.submissionData = this.submissionData.filter((data) => data.id !== id);
        }
      });
      this.data = this.submissionData;
    }
  }

  ngOnDestroy() {
    if (this.langChangeSub) {
      this.langChangeSub.unsubscribe();
    }
  }

}

import { Component} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {LocalDataSource} from "ng2-smart-table";
import {SubmissionDto} from "../../dto/submission.dto";
import {ActivatedRoute, Router} from '@angular/router';
import {ContestApiService} from '../../services/contestapi.service';
import {ContestApiDTO} from '../../dto/api.contest.dto';
import {ProblemApiService} from '../../services/problemapi.service';
import {SubmissionApiService} from '../../services/submissionapi.service';
import {ProblemApiDto} from '../../dto/api.problem.dto';

const PREDIFINED_PUBLIC_CONTEST_ID = "7";

@Component({
  templateUrl: './menu.component.html',
})

export class MenuComponent {
  // TODO: FIX this, should be in the environment, and use relative paths
  API_URL_HEAD = 'http://localhost:8080/api/v1/';
  problemIdSelected:string;
  contestId:string;
  currentContest:ContestApiDTO;
  problemCreated:ProblemApiDto;
  hiddenAllProblems:boolean = false;
  hiddenAddProblems:boolean = true;
  hiddenEditProblems:boolean = true;
  hiddenSubmissions:boolean = true;
  hiddenAddExistingProblemToContest:boolean = true;
  hiddenRanking:boolean = true;

  problemsOutOfActualContest:ProblemApiDto[];
  problemsAtActualContest:string[];
  allProblems:ProblemApiDto[];

  localDataSource:LocalDataSource;
  submissionsList:SubmissionDto[];
//add
  fileSelected:FileList;
  zipFileToCreateProblem:File;
  makeProblemPublic:boolean;
  isStudent:boolean;
  /* https://stackoverflow.com/questions/40430893/ng2-smart-table-create-button-not-working-correctly
   */

  settingsTable = {
    actions:{
      add:false,
      edit:false
    },
    mode: 'inline',
    delete: {
      deleteButtonContent: '<i class="nb-trash"></i>',
      confirmDelete: true
    },
    pager: {
      perPage: 5
    },
    columns: {
      id: {
        title: $localize `:@@idColumn:`,
        editable: false,
      },
      nombreEjercicio: {
        title: $localize `:@@nameColumn:`
      },
      problemURLpdf: {
        title: $localize `:@@problemViewerPDF:`,
        type:'html',
        valuePrepareFunction: (data) =>{
          let url:string = data;
          url = url.replace('/API/v1/','');
          return '<a href=\'' + this.API_URL_HEAD + url + '\' target=\'_blank\' \>' + $localize `:@@problemViewerPDF:` + '</a>';
          let a:string = 'target=\'_blank\' \>';
        }
      }
    },
  };
  settingsTableStudent = {
    actions:false,
    mode: 'inline',
    delete: {
      deleteButtonContent: '<i class="nb-trash"></i>',
      confirmDelete: true
    },
    pager: {
      perPage: 5
    },
    columns: {
      id: {
        title: $localize `:@@idColumn:`,
        editable: false,
      },
      nombreEjercicio: {
        title: $localize `:@@nameColumn:`
      },
      problemURLpdf: {
        title: $localize `:@@problemViewerPDF:`,
        type:'html',
        valuePrepareFunction: (data) =>{
          let url:string = data;
          url = url.replace('/API/v1/','');
          return '<a href=\'' + this.API_URL_HEAD + url + '\' target=\'_blank\' \>' + $localize `:@@problemViewerPDF:` + '</a>';
          let a:string = 'target=\'_blank\' \>';
        }
      }
    },
  };
  settingsAllSubmissionsTable = {
    actions:{
      add:false,
      edit:false
    },
    mode: 'inline',
    delete: {
      deleteButtonContent: '<i class="nb-trash"></i>',
      confirmDelete: true
    },
    pager: {
      perPage: 10
    },
    columns: {
      id:{
        title: $localize `:@@idColumn:`,
      },
      team: {
        title: $localize `:@@teamColumn:`,
        valuePrepareFunction: (data) =>{
          return data.nombreEquipo;
        }
      },
      problem: {
        title: $localize `:@@problemColumn:`,
        valuePrepareFunction: (data) =>{
          return data.nombreEjercicio;
        }
      },
      language: {
        title: $localize `:@@languageColumn:`,
        valuePrepareFunction: (data) =>{
          return data.nombreLenguaje;
        }
      },
      timestamp: {
        title: $localize `:@@dateColumn:`,
        valuePrepareFunction: (data) =>{
          let hour:string = new Date(data).toLocaleTimeString();
          let date:string = new Date(data).toLocaleDateString();

          return date + " " + hour;
        }
      },
      resultado: {
        title: $localize `:@@resultColumn:`,
        type: 'html',
        valuePrepareFunction: (data) =>{
          //format color
          if (data!=null && data != "")
            return data;
          else
            return $localize `:@@notEvaluatedText:`;
        }
      },
    },
  };

  constructor(private activatedRoute: ActivatedRoute, private http: HttpClient, private router:Router,
              private contestApiService: ContestApiService,
              private problemApiService: ProblemApiService,
              private submissionApiService: SubmissionApiService) {
    this.contestId = this.activatedRoute.snapshot.params.contestId;
    this.isStudent = this.activatedRoute.snapshot.routeConfig.path.startsWith("student");

  }

  ngOnInit() {
    this.problemsOutOfActualContest = [];
    this.problemsAtActualContest = [];
    this.allProblems = [];
    this.contestApiService.getSelectedContest(this.contestId).subscribe(response => {
      this.currentContest = response;
      this.localDataSource = new LocalDataSource(this.currentContest.listaProblemas);
      this.currentContest.listaProblemas.forEach(x =>{
        this.problemsAtActualContest.push(''+x.id);
      })

      this.problemApiService.getAllProblems().subscribe(data => {
        this.allProblems = data;
        this.allProblems.forEach(problem=>{
          if(!this.problemsAtActualContest.includes(''+problem.id)){
            this.problemsOutOfActualContest.push(problem);
          }
        });
      });

    });
  }

  showAllProblems(){
    this.contestApiService.getSelectedContest(this.contestId).subscribe(response =>{
      this.currentContest = response;
      this.localDataSource = new LocalDataSource(this.currentContest.listaProblemas);
      this.hiddenAddExistingProblemToContest = true;
      this.hiddenAllProblems = false;
      this.hiddenAddProblems = true;
      this.hiddenEditProblems = true;
      this.hiddenSubmissions = true;
      this.hiddenRanking = true;

    });

  }

  showCreateProblemWindow(){
    this.hiddenAllProblems = true;
    this.hiddenAddProblems = false;
    this.hiddenEditProblems = true;
    this.hiddenSubmissions = true;
    this.hiddenAddExistingProblemToContest = true;
    this.hiddenRanking = true;

  }

  async showSubmissions() {
    let i = 0;
    this.hiddenAllProblems = true;
    this.hiddenAddProblems = true;
    this.hiddenEditProblems = true;
    this.hiddenSubmissions = false;
    this.hiddenAddExistingProblemToContest = true;
    this.hiddenRanking = true;

    while(!this.hiddenSubmissions){
      console.log("Polling Execution number :  "  + i);
      this.getSubmissionsBack();
      console.log("Sleeping...");
      await new Promise(resolve => setTimeout(resolve, 5000));
      console.log("Wake up!");
      i++;
    }
  }

  showAddProblemToContest() {
    this.hiddenAllProblems = true;
    this.hiddenAddProblems = true;
    this.hiddenEditProblems = true;
    this.hiddenSubmissions = true;
    this.hiddenAddExistingProblemToContest = false;
    this.hiddenRanking = true;

  }

  showRanking(){
    this.hiddenAllProblems = true;
    this.hiddenAddProblems = true;
    this.hiddenEditProblems = true;
    this.hiddenSubmissions = true;
    this.hiddenAddExistingProblemToContest = true;
    this.hiddenRanking = false;
  }
  save(){

    if(this.zipFileToCreateProblem == null) {
      alert($localize `:@@alertFillFields:`);
    }else {
      if (window.confirm($localize `:@@confirmationSave:`)) {
        this.problemApiService.createProblemFromZip(this.contestId, this.zipFileToCreateProblem, "", "7").subscribe(data =>{
          this.problemCreated = data;
          if(this.makeProblemPublic && this.contestId != PREDIFINED_PUBLIC_CONTEST_ID){
            this.contestApiService.addProblemToContest(''+this.problemCreated.id, PREDIFINED_PUBLIC_CONTEST_ID).subscribe(); // "1" --> number of predifined public contest, actually default contest have id=7
          }
          alert($localize `:@@alertProblemSaved:` + ': ' + this.problemCreated.nombreEjercicio);
          this.zipFileToCreateProblem = null;
          this.hiddenAddProblems = true;
        });
      }
    }
  }

  getSubmissionsBack(){
/*    this.submissionApiService.getAllSubmissions(null,this.contestId).subscribe(data=>{
      this.submissionsList = data;
    });*/
    this.submissionApiService.getAllSubmissions(null,this.contestId).subscribe(data=>{
      this.submissionsList = data;
    });
  }


  deleteFromTable(event){
    if (window.confirm($localize `:@@confirmationDelete:`)) {
      this.problemApiService.deleteProblem(event.data.id).subscribe();

      event.confirm.resolve(event.newData);

    } else {
      event.confirm.reject();
    }
  }

  selectedProblemFromTable(event) {
    let actualProblemId = event.data.id;
    if(this.isStudent) {
      this.router.navigate(['/student/contest/' + this.contestId + '/problem/' + actualProblemId]);
    }else{
      this.router.navigate(['/teacher/contest/' + this.contestId + '/problem/' + actualProblemId]);
    }
  }

  manageZipFile(event) {
    if (event.target.files.length > 0) {
      this.zipFileToCreateProblem = event.target.files[0];
    }
  }


  addProblemToContest() {
    this.contestApiService.addProblemToContest(this.problemIdSelected, this.contestId).subscribe();
    alert($localize `:@@alertProblemAdded:`);
    this.hiddenAddExistingProblemToContest = true;
    this.hiddenAllProblems = false;
    this.router.navigate(['/teacher/menu'])

  }

  deleteSubmissionFromTable(event){
    if (window.confirm($localize `:@@confirmationDelete:`)) {
      this.submissionApiService.deleteSubmission(event.data.id).subscribe();
      event.confirm.resolve(event.newData);

    } else {
      event.confirm.reject();
    }
  }

  selectedSubmissionFromTable(event) {
    let actualSubmissionId = event.data.id;
    if(this.isStudent) {
      this.router.navigate(['/student/contest/' + this.contestId + '/submission/' + actualSubmissionId]);
    }else{
      this.router.navigate(['/teacher/contest/' + this.contestId + '/submission/' + actualSubmissionId]);
    }
  }

  updateCheckBoxValue(value: boolean) {
    this.makeProblemPublic = value;
  }
}

import { Component, ElementRef, ViewChild} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {LocalDataSource} from "ng2-smart-table";
import {ProblemDto} from '../../../dto/problem.dto';
import {ContestApiService} from '../../../services/contestapi.service';
import {ContestApiDTO} from '../../../dto/api.contest.dto';
import {ActivatedRoute, Router} from '@angular/router';
import {TeamApiService} from '../../../services/teamapi.service';
import {TeamApiDTO} from '../../../dto/api.team.dto';


@Component({
  templateUrl: './contestList.component.html',
})

export class ContestListComponent {

  constructor(private activatedRoute: ActivatedRoute, private http: HttpClient, private router:Router,
              private contestApiService: ContestApiService, private teamApiService:TeamApiService) {
    this.isStudent = this.activatedRoute.snapshot.routeConfig.path.startsWith("student");

  }
  currentTeam:TeamApiDTO;
  localDataSource:LocalDataSource;
  createdContest:ContestApiDTO;
  contestList: ContestApiDTO[];
  isStudent:boolean;

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
        type:'text',
        editable: false,
   //     renderComponent: LinkGeneratorComponent
      },
      nombreContest: {
        title: $localize `:@@nameColumn:`,
        editor: {
          type: 'text'
        }
      },
      descripcion: {
        title: $localize `:@@descriptionColumn:`,
        editor:{
          type:'textarea'
        }
      },
      startDateTime: {
        title: $localize `:@@startDateContest:`,
        valuePrepareFunction: (data) =>{
          let hour:string = new Date(data).toLocaleTimeString();
          let date:string = new Date(data).toLocaleDateString();

          return date + " " + hour;
        }
      },
      endDateTime: {
        title: $localize `:@@endDateContest:`,
        valuePrepareFunction: (data) =>{
          let hour:string = new Date(data).toLocaleTimeString();
          let date:string = new Date(data).toLocaleDateString();

          return date + " " + hour;
        }
      },
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
        type:'text',
        editable: false,
        //     renderComponent: LinkGeneratorComponent
      },
      nombreContest: {
        title: $localize `:@@nameColumn:`,
        editor: {
          type: 'text'
        }
      },
      descripcion: {
        title: $localize `:@@descriptionColumn:`,
        editor:{
          type:'textarea'
        }
      },
      startDateTime: {
        title: $localize `:@@startDateContest:`,
        valuePrepareFunction: (data) =>{
          let hour:string = new Date(data).toLocaleTimeString();
          let date:string = new Date(data).toLocaleDateString();

          return date + " " + hour;
        }
      },
      endDateTime: {
        title: $localize `:@@endDateContest:`,
        valuePrepareFunction: (data) =>{
          let hour:string = new Date(data).toLocaleTimeString();
          let date:string = new Date(data).toLocaleDateString();

          return date + " " + hour;
        }
      },
    },
  };

  ngOnInit(){
    if(this.isStudent){
      this.teamApiService.getSelectedTeam("7").subscribe(data => {
        this.currentTeam = data;
        this.contestList = this.currentTeam.listaContestsParticipados;
        this.contestList.forEach(contestApiDto => {
          let actualId = contestApiDto.id;
          // contestApiDto.id = this.generateHtmlLink(actualId);
        })
        this.localDataSource = new LocalDataSource(this.contestList);

      });
    }else {
      this.contestApiService.getAllContests().subscribe(data => {
        this.contestList = data;
        this.contestList.forEach(contestApiDto => {
          let actualId = contestApiDto.id;
          // contestApiDto.id = this.generateHtmlLink(actualId);
        })
        this.localDataSource = new LocalDataSource(this.contestList);

      });
    }
  }

  goToCreateContest(){
    this.router.navigate(['/teacher/createcontest']);

  }

  goToEditContest(){
    this.router.navigate(['/teacher/editcontest']);

  }

/*  addContestFromTable(event){
    if(event.newData.contestName === "" || event.newData.descripcion === ""){
      alert($localize `:@@alertFillFields:`);
    }else {
      if (window.confirm($localize `:@@confirmationSave:`)) {

        this.contestApiService.createContest(event.newData.nombreContest, event.newData.descripcion, "6").subscribe(data=> {
            this.createdContest = data;
            event.newData.id = this.createdContest.id;
            event.confirm.resolve(event.newData);

          }
        ); //team=pavloXd

      } else {
        event.confirm.reject();
      }
    }
  }*/

/*  editContestFromTable(event){
    if(event.newData.contestName === "" || event.newData.descripcion === ""){
      alert($localize `:@@alertFillFields:`);
    }else {
      if (window.confirm($localize `:@@confirmationEdit:`)) {
        this.contestApiService.updateContest(event.newData.id, event.newData.nombreContest, event.newData.descripcion, "6").subscribe(data=> {
            this.createdContest = data;
            event.confirm.resolve(event.newData);

          }
        ); //team=pavloXd

      } else {
        event.confirm.reject();
      }
    }
  }*/

  deleteContestFromTable(event){
    if (window.confirm($localize `:@@confirmationDelete:`)) {
        this.contestApiService.deleteContest(event.data.id).subscribe();
        event.confirm.resolve(event.newData);

      } else {
        event.confirm.reject();
      }
    }

  selectedContestFromTable(event) {
      let actualId = event.data.id;
      if(this.isStudent){
        this.router.navigate(['/student/contest/' + actualId]);
      }else {
        this.router.navigate(['/teacher/contest/' + actualId]);
      }
  }
}

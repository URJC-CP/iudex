import { Component} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {LocalDataSource} from "ng2-smart-table";
import {ActivatedRoute, Router} from '@angular/router';
import {ContestApiDTO} from '../../dto/api.contest.dto';
import {ContestApiService} from '../../services/contestapi.service';


@Component({
  templateUrl: './publicproblems.component.html',
})

export class PublicProblemsComponent {
  API_URL_HEAD = 'http://localhost:4200/api/';
  problemIdSelected:string;
  contestId:string = "8"; //id contest public por defecto
  currentContest:ContestApiDTO;
  localDataSource:LocalDataSource;


  settingsTable = {
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

  constructor(private activatedRoute: ActivatedRoute, private http: HttpClient, private router:Router,
              private contestApiService: ContestApiService){

    this.contestApiService.getSelectedContest(this.contestId).subscribe(response => {
      this.currentContest = response;

      this.localDataSource = new LocalDataSource(this.currentContest.listaProblemas);
    });
  }

  ngOnInit() {


    }

  selectedProblemFromTable(event) {
    let actualProblemId = event.data.id;
    this.router.navigate(['/public/contest/' + this.contestId + '/problem/' + actualProblemId]);
  }
}

import {Component, ElementRef, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {LocalDataSource} from "ng2-smart-table";
import {ActivatedRoute, Router} from '@angular/router';
import {SubmissionApiService} from '../../services/submissionapi.service';
import {SubmissionApiDTO} from '../../dto/api.submission.dto';


@Component({
  templateUrl: './publicsubmissions.component.html',
})

export class PublicSubmissionsComponent {

  contestId:string = "7"; //id contest public por defecto
  localDataSource:LocalDataSource;
  submissionsList:SubmissionApiDTO[];
  settingsTable = {
    actions: false,
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
              private submissionApiService: SubmissionApiService){

    this.submissionApiService.getAllSubmissions(null,this.contestId).subscribe(data=>{
      this.submissionsList = data;
      this.localDataSource = new LocalDataSource(this.submissionsList);

    });

  }

  selectedSubmissionFromTable(event) {
    let actualSubmissionId = event.data.id;
    this.router.navigate(['/public/contest/' + this.contestId + '/submission/' + actualSubmissionId]);
  }
}

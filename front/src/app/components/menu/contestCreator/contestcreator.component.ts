import { Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {ContestApiService} from '../../../services/contestapi.service';


@Component({
  templateUrl: './contestcreator.component.html',
})

export class ContestCreatorComponent {

  contestName:string;
  contestDescription:string;
  startDateContest:Date;
  endDateContest:Date;

  constructor(private activatedRoute: ActivatedRoute, private http: HttpClient, private router:Router,
              private contestService: ContestApiService) {
  }


  save(){
    let startDate = new Date(this.startDateContest);
    let endDate = new Date(this.endDateContest);
    this.contestService.createContest(this.contestName, this.contestDescription, "7",startDate.getTime(), endDate.getTime()).subscribe(data=>{
        this.router.navigate(['/teacher/menu']);
    }, error => {
      alert("API ERROR " + error.status);
    })


  }
}

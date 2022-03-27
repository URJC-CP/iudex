import { Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {ProblemApiService} from '../../services/problemapi.service';
import {ProblemApiDto} from '../../dto/api.problem.dto';

@Component({
  templateUrl: './problemviewer.component.html',
})

export class ProblemViewerComponent {

  problemId:string;
  contestId:string;
  actualProblem:ProblemApiDto;
  pdfCompletePath:string;
  isStudent:boolean;
  isPublic: boolean;

  constructor(private activatedRoute: ActivatedRoute, private http: HttpClient, private router:Router,
              private problemApiService: ProblemApiService) {
    this.problemId = this.activatedRoute.snapshot.params.problemId;
    this.contestId = this.activatedRoute.snapshot.params.contestId;
    //solucion preventiva hasta que se sepa como va el rol de usuarios
    this.isStudent = this.activatedRoute.snapshot.routeConfig.path.startsWith("student");
    this.isPublic = this.activatedRoute.snapshot.routeConfig.path.startsWith("public");
  }

  ngOnInit(): void {
    this.problemApiService.getSelectedProblem(this.problemId).subscribe(data =>{
      this.actualProblem = data;
      let incompletePath = this.actualProblem.problemURLpdf;
      this.pdfCompletePath = incompletePath.replace("/API/v1/","http://localhost:4200/api/");
      document.getElementById("pdfObject").setAttribute("data", this.pdfCompletePath);
    })
  }

  goToEditProblem() {
    this.router.navigate(['/teacher/contest/' + this.contestId + '/problem/' + this.problemId + '/edit']);
  }

  deleteProblem() {
    if (window.confirm($localize `:@@confirmationDelete:`)) {
      this.problemApiService.deleteProblem(this.problemId).subscribe();
      this.router.navigate(['/teacher/contest/' + this.contestId]);
    }
  }

  goToResolveProblem() {
    this.router.navigate(['/teacher/contest/' + this.contestId +'/problem/' + this.problemId + '/submission' ]);
  }

  goToAddTestCase() {
    this.router.navigate(['/teacher/contest/' + this.contestId +'/problem/' + this.problemId + '/samples/add' ]);
  }

  goToEditTestCase() {
    if(this.actualProblem.samples.length != 0) {
      this.router.navigate(['/teacher/contest/' + this.contestId + '/problem/' + this.problemId + '/samples/edit']);
    }else{
      window.alert($localize `:@@noSamplesToUpdate:`)
    }
  }

  goToDeleteTestCase() {
    if(this.actualProblem.samples.length != 0) {
      this.router.navigate(['/teacher/contest/' + this.contestId + '/problem/' + this.problemId + '/samples/delete']);
    }else{
      window.alert($localize `:@@noSamplesToDelete:`)
    }
  }
}

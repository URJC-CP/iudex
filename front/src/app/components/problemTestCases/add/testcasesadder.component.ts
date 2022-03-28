import { Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {ProblemApiDto} from '../../../dto/api.problem.dto';
import {ProblemApiService} from '../../../services/problemapi.service';
import {SamplesApiDTO} from '../../../dto/api.samples.dto';


@Component({
  templateUrl: './testcasesadder.component.html',
})

export class TestCasesAdderComponent {

  problemId:string;
  contestId:string;
  actualProblem:ProblemApiDto;
  isStudent:boolean;
  isPublic: boolean;
  testCases:SamplesApiDTO[];

  testCaseName:string = "";
  testCaseInputFile:File;
  testCaseOutputFile:File;
  testCaseIsPublic:boolean = true;

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
      this.testCases = this.actualProblem.samples;
    })
  }

  manageInputFile(event) {
    if (event.target.files.length > 0) {
      this.testCaseInputFile = event.target.files[0];
    }
  }

  manageOutputFile(event) {
    if (event.target.files.length > 0) {
      this.testCaseOutputFile = event.target.files[0];
    }
  }

  addTestCase() {
    if (window.confirm($localize `:@@confirmationAdd:`)) {

      this.problemApiService.createSampleForProblem(this.problemId, this.testCaseInputFile, this.testCaseOutputFile, this.testCaseIsPublic, this.testCaseName).subscribe(data => {
        alert($localize `:@@testCaseAddedOK:`);
        if (!window.confirm($localize `:@@confirmationOtherAdd:`)) {
          this.router.navigate(['teacher/contest/' + this.contestId + '/problem/' + this.problemId]);
        }else{
          this.testCaseName= "";
          this.testCaseInputFile = null;
          this.testCaseOutputFile = null;
          this.testCaseIsPublic = true;
        }
      });
    }
  }

}

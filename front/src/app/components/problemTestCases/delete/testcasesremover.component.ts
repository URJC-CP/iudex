import { Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {ProblemApiDto} from '../../../dto/api.problem.dto';
import {ProblemApiService} from '../../../services/problemapi.service';
import {SamplesApiDTO} from '../../../dto/api.samples.dto';


@Component({
  templateUrl: './testcasesremover.component.html',
})

export class TestCasesRemoverComponent {

  problemId:string;
  contestId:string;
  actualProblem:ProblemApiDto;
  isStudent:boolean;
  isPublic: boolean;
  testCases:SamplesApiDTO[];
  selectedTestCase:SamplesApiDTO;
  selectedTestCaseId:string = "";
  selectedTestCaseOutput:string = "";
  selectedTestCaseInput:string = "";
  selectedTestCaseName:string = "";

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

  onChangeSelectTestCasesToDelete(value) {
    this.selectedTestCase = value;
    this.selectedTestCaseId =''+ this.selectedTestCase.id;
    this.selectedTestCaseOutput = this.selectedTestCase.outputText;
    this.selectedTestCaseInput = this.selectedTestCase.inputText;
    this.selectedTestCaseName = this.selectedTestCase.name;
  }

  deleteTestCase() {
    if (window.confirm($localize `:@@confirmationDelete:`)) {
      this.problemApiService.deleteSampleFromProblem(this.problemId, this.selectedTestCaseId).subscribe(data => {
        alert($localize `:@@testCaseDeletedOK:`);
        if (this.testCases.length == 1 || !window.confirm($localize `:@@confirmationOtherDelete:`)) {
          this.router.navigate(['teacher/contest/' + this.contestId + '/problem/' + this.problemId]);
        }else{
          this.ngOnInit();
          this.selectedTestCase = null;
          this.selectedTestCaseId = "";
          this.selectedTestCaseOutput = "";
          this.selectedTestCaseInput = "";
          this.selectedTestCaseName = "";
        }
      });
    }
  }

}

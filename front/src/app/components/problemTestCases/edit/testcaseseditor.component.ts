import { Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {ProblemApiDto} from '../../../dto/api.problem.dto';
import {ProblemApiService} from '../../../services/problemapi.service';
import {SamplesApiDTO} from '../../../dto/api.samples.dto';


@Component({
  templateUrl: './testcaseseditor.component.html',
})

export class TestCasesEditorComponent {

  problemId:string;
  contestId:string;
  actualProblem:ProblemApiDto;
  isStudent:boolean;
  isPublic: boolean;
  isntFilled: boolean;
  testCases:SamplesApiDTO[];

  selectedTestCaseInput:string = "";
  selectedTestCaseOutput:string = "";
  selectedTestCaseIsPublic:boolean = true;
  selectedTestCase:SamplesApiDTO;
  selectedTestCaseId:string = "";
  selectedTestCaseName:string = "";

  inputFileToSend :File;
  outputFileToSend: File;

  constructor(private activatedRoute: ActivatedRoute, private http: HttpClient, private router:Router,
              private problemApiService: ProblemApiService) {
    this.problemId = this.activatedRoute.snapshot.params.problemId;
    this.contestId = this.activatedRoute.snapshot.params.contestId;
    //solucion preventiva hasta que se sepa como va el rol de usuarios
    this.isStudent = this.activatedRoute.snapshot.routeConfig.path.startsWith("student");
    this.isPublic = this.activatedRoute.snapshot.routeConfig.path.startsWith("public");
    this.isntFilled = true;
  }

  ngOnInit(): void {
    this.problemApiService.getSelectedProblem(this.problemId).subscribe(data =>{
      this.actualProblem = data;
      this.testCases = this.actualProblem.samples;
    })
  }

  manageInputFile(event) {
    if (event.target.files.length > 0) {
      this.inputFileToSend = event.target.files[0];
    }
  }

  manageOutputFile(event) {
    if (event.target.files.length > 0) {
      this.outputFileToSend = event.target.files[0];
    }
  }

  onChangeSelectTestCasesToEdit(value) {
    this.selectedTestCase = value;
    this.selectedTestCaseId =''+ this.selectedTestCase.id;
    this.selectedTestCaseOutput = this.selectedTestCase.outputText;
    this.selectedTestCaseInput = this.selectedTestCase.inputText;
    this.selectedTestCaseName = this.selectedTestCase.name;
    this.isntFilled = false;
  }

  updateTestCase() {
    if (window.confirm($localize `:@@confirmationEdit:`)) {
      this.problemApiService.updateSampleForProblem(this.problemId, this.selectedTestCaseId, this.inputFileToSend, this.outputFileToSend, this.selectedTestCaseIsPublic, this.selectedTestCaseName).subscribe(data => {
        alert($localize `:@@testCaseEditedOK:`);
        if (!window.confirm($localize `:@@confirmationOtherEdit:`)) {
          this.router.navigate(['teacher/contest/' + this.contestId + '/problem/' + this.problemId]);
        }else{
          this.ngOnInit();
          this.selectedTestCase = null;
          this.inputFileToSend = null;
          this.outputFileToSend = null;
          this.selectedTestCaseId = "";
          this.selectedTestCaseOutput = "";
          this.selectedTestCaseInput = "";
          this.selectedTestCaseName = "";
          this.selectedTestCaseIsPublic = true;
          this.isntFilled = true;
        }
      });
    }
  }

}

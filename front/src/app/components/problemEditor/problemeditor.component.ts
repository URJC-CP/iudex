import { Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {ProblemApiService} from '../../services/problemapi.service';
import {ProblemApiDto} from '../../dto/api.problem.dto';
import {utf8Encode} from '@angular/compiler/src/util';

@Component({
  templateUrl: './problemeditor.component.html',
})

export class ProblemEditorComponent {

  problemId:string;
  contestId:string;
  actualProblem:ProblemApiDto;
  problemName:string;
  hideEditByFieldsOption:boolean = true;
  hideEditByZipOption:boolean = true;
  hideSaveButton:boolean = true;
  selectedOption:string;
  zipFileToCreateProblem:File;
  pdfProblem:File;
  problemEdited: ProblemApiDto;
  problemTimeout:string;

  constructor(private activatedRoute: ActivatedRoute, private http: HttpClient, private router:Router,
              private problemApiService: ProblemApiService) {
    this.problemId = this.activatedRoute.snapshot.params.problemId;
    this.contestId = this.activatedRoute.snapshot.params.contestId;
    this.problemApiService.getSelectedProblem(this.problemId).subscribe(data =>{
      this.actualProblem = data;
      this.problemName = this.actualProblem.nombreEjercicio;
      this.problemTimeout = this.actualProblem.timeout;
    })
  }

  onChangeRadioGroup(value) {
    this.hideSaveButton = false;
    this.hideEditByFieldsOption = value== "zip";
    this.hideEditByZipOption = value == "fields";
    this.selectedOption = value;
  }

  manageZipFile(event) {
    if (event.target.files.length > 0) {
      this.zipFileToCreateProblem = event.target.files[0];
    }
  }

  managePdfFile(event) {
    if (event.target.files.length > 0) {
      this.pdfProblem = event.target.files[0];
    }
  }

  save(){
    if(this.selectedOption!= null && this.hideEditByFieldsOption){
      if(this.zipFileToCreateProblem == null) {
        alert($localize `:@@alertFillFields:`);
      }else {
        if (window.confirm($localize `:@@confirmationSave:`)) {
          this.problemApiService.updateProblemFromZip(this.problemId, this.contestId,this.zipFileToCreateProblem,"", "7").subscribe(data =>{
            this.problemEdited = data;
            alert($localize `:@@alertProblemSaved:` + ': ' + this.problemEdited.nombreEjercicio);
            this.zipFileToCreateProblem = null;
            this.router.navigate(['/teacher/contest/' + this.contestId]);
          });
        }
      }
    }else{
      if(this.problemName!=null && this.problemName!= "" &&
        (this.problemName != this.actualProblem.nombreEjercicio || this.pdfProblem != null || this.problemTimeout != this.actualProblem.timeout)){
        if (window.confirm($localize `:@@confirmationEdit:`)) {
          this.problemApiService.updateProblem(this.problemId, this.pdfProblem, this.problemName, "7", this.problemTimeout).subscribe(data =>{
            this.problemEdited = data;
            alert($localize `:@@alertProblemSaved:` + ': ' + this.problemEdited.nombreEjercicio);
            this.zipFileToCreateProblem = null;
            this.router.navigate(['/teacher/contest/' + this.contestId]);
          });
        }
      }else{
        alert($localize `:@@alertEditProblem:`);
      }
    }
  }
}

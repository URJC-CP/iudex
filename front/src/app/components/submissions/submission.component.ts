import { Component, ViewChild } from '@angular/core';
import {
  MonacoEditorComponent,
  MonacoEditorConstructionOptions,
  MonacoEditorLoaderService,
  MonacoStandaloneCodeEditor
} from '@materia-ui/ngx-monaco-editor';
import {ActivatedRoute, Router} from '@angular/router';
import {SubmissionDto} from '../../dto/submission.dto';
import {HttpClient} from '@angular/common/http';
import {ProblemApiDto} from '../../dto/api.problem.dto';
import {ProblemApiService} from '../../services/problemapi.service';
import {SubmissionApiService} from '../../services/submissionapi.service';

@Component({
  templateUrl: './submission.component.html',
})

export class SubmissionComponent {
  @ViewChild(MonacoEditorComponent, { static: false }) monacoComponent: MonacoEditorComponent;
  codeFile: File;
  problemReference:string;
  contestId:string;
  isStudent:boolean;
  actualProblem:ProblemApiDto;
  code: string | ArrayBuffer= '';
  submissionResponse:SubmissionDto;
  userTheme: string = "vs-dark";
  userLanguage: string = "pascal";
  availableLanguages: string[] = [
    "pascal",
    "java"
  ];
  editorOptions: MonacoEditorConstructionOptions = {
    theme: this.userTheme,
    language: this.userLanguage,
    roundedSelection: true,
    autoIndent: true
  };
  editor: MonacoStandaloneCodeEditor;
  constructor(private activatedRoute: ActivatedRoute,private monacoLoaderService: MonacoEditorLoaderService, private http: HttpClient, private router:Router,
              private problemApiService: ProblemApiService, private submissionApiService:SubmissionApiService) {
    this.problemReference = this.activatedRoute.snapshot.params.problemId;
    this.contestId = this.activatedRoute.snapshot.params.contestId;
    this.isStudent = this.activatedRoute.snapshot.routeConfig.path.startsWith("student");

    this.problemApiService.getSelectedProblem(this.problemReference).subscribe(data=>{
      this.actualProblem = data;
    })


  }

  ngOnInit(): void { }

  editorInit(editor: MonacoStandaloneCodeEditor) {
    this.editor = editor
  }

  changeLanguage(event) {
    this.editorOptions = {...this.editorOptions, language: event}
  }

  filefunct(event) {

    if (event.target.files.length > 0) {
     this.codeFile = event.target.files[0];
      this.readInputFile(this.codeFile);
    }
  }

  readInputFile(file: File) {
    var fileReader:FileReader = new FileReader();
    this.code = "";
    fileReader.readAsText(file);
    fileReader.onloadend = (e) => {
      console.log("FileReader has finished the file reading!")
      this.code = fileReader.result;
    }
  }

  sendSubmission(){

    let finalCodeFile:File = new File([this.code],this.codeFile.name);
      this.submissionApiService.createSubmission(finalCodeFile, this.contestId, '1' ,''+this.actualProblem.id, "7").subscribe(response=>{
      this.submissionResponse = response;

        if (this.submissionResponse != null) {
          alert($localize `:@@alertReceivedSubmission:`);
          if(this.isStudent)
            this.router.navigate(['/student/menu']);
          else
            this.router.navigate(['/teacher/contest/' + this.contestId])
        } else {
          alert($localize `:@@alertErrorSubmission:`);
        }
      }
    );



  }




}

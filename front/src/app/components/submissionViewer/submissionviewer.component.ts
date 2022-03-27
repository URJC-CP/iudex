import {Component,  ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {SubmissionApiService} from '../../services/submissionapi.service';
import {SubmissionApiDTO} from '../../dto/api.submission.dto';
import {MonacoEditorComponent, MonacoEditorConstructionOptions, MonacoStandaloneCodeEditor} from '@materia-ui/ngx-monaco-editor';
import {ResultApiDTO} from '../../dto/api.resultapi.dto';

@Component({
  templateUrl: './submissionviewer.component.html',
})

export class SubmissionViewerComponent {
  @ViewChild(MonacoEditorComponent, { static: false }) monacoComponent: MonacoEditorComponent;
  submissionId:string;
  contestId:string;
  problemId:string;
  actualSubmission:SubmissionApiDTO;
  isStudent:boolean;
  isPublic:boolean;
  code:string;
  submissionDate:string;
  editor: MonacoStandaloneCodeEditor;
  editorOptions: MonacoEditorConstructionOptions = {
    theme: "vs-dark",
    language: '',
    roundedSelection: true,
    autoIndent: true,
    readOnly:true

  };
  testCasesResults:ResultApiDTO[];
  testCasesOK:number=0;
  testCasesKO:number=0;
  testCasesWithoutRevision:number=0;
  pieChartLabels:string[] = ['OK','KO', $localize `:@@notEvaluatedText:`];
  //pieChartData:number[] =[1,2,0];
  pieChartData:number[] =[];

  constructor(private activatedRoute: ActivatedRoute, private http: HttpClient, private router:Router,
              private submissionApiService:SubmissionApiService) {
    this.submissionId = this.activatedRoute.snapshot.params.submissionId;
    this.contestId = this.activatedRoute.snapshot.params.contestId;
    //solucion preventiva hasta que se sepa como va el rol de usuarios
    this.isStudent = this.activatedRoute.snapshot.routeConfig.path.startsWith("student");
    this.isPublic = this.activatedRoute.snapshot.routeConfig.path.startsWith("public");
  }

  ngOnInit(): void {
    this.submissionApiService.getSubmissionWithResults(this.submissionId).subscribe(data =>{
      this.actualSubmission = data;
      this.problemId = ''+this.actualSubmission.problem.id;
      this.testCasesResults = this.actualSubmission.results;
      //set original code language to the editor
      this.editorOptions = {...this.editorOptions, language: this.actualSubmission.language.nombreLenguaje};
      this.code = this.testCasesResults[0].codigo;
      this.submissionDate = this.timestampToDate(this.actualSubmission.timestamp);

      //para checkear distintos valores (eliminar cuando este ok el back)
      // this.testCasesResults[0].resultadoRevision = "ok";
      // this.testCasesResults[1].resultadoRevision = "ko";
      // this.testCasesResults[2].resultadoRevision = "ko";
      // this.testCasesResults[3].resultadoRevision = "";

      this.testCasesResults.forEach(testCaseResult =>{
        if(testCaseResult.resultadoRevision == "ok") {
          this.testCasesOK++;
        }else if(testCaseResult.resultadoRevision == "ko"){
          this.testCasesKO++;
        }else{
        //  testCaseResult.resultadoRevision = $localize`:@@notEvaluatedText:`
          this.testCasesWithoutRevision++;
        }
      });
      this.pieChartData = [this.testCasesOK, this.testCasesKO, this.testCasesWithoutRevision];

    })

  }
  editorInit(editor: MonacoStandaloneCodeEditor) {
    this.editor = editor
  }

  timestampToDate(timestampValue:number):string{
    let hour:string = new Date(timestampValue).toLocaleTimeString();
    let date:string = new Date(timestampValue).toLocaleDateString();

    return date + " " + hour;
  }

  viewProblemDetails(event) {
    if(this.isPublic){
      this.router.navigate(['public/contest/' + this.contestId + '/problem/' + this.problemId])

    }else if(this.isStudent){
      this.router.navigate(['student/contest/' + this.contestId + '/problem/' + this.problemId])

    }else{
      this.router.navigate(['teacher/contest/' + this.contestId + '/problem/' + this.problemId])

    }
  }

  checkValue(resultadoRevision: string, checkWith: string) {
    return resultadoRevision == checkWith;
  }
}



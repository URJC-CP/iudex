import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiCommonService} from './apicommon.service';

@Injectable({
  providedIn: 'root'
})
export class SubmissionApiService extends ApiCommonService{

  constructor(private http: HttpClient) {
    super();
  }

  createSubmission(codigo:File, contestId:string, lenguaje:string, problemId:string, teamId:string): Observable<any>{

    let formData:FormData = new FormData();
    formData.append('codigo', codigo);

    let httpOptions = {
      params: new HttpParams()
        .set('contestId', contestId)
        .set('lenguaje', lenguaje)
        .set('problemId', problemId)
        .set('teamId', teamId)
    };

    return this.http.post(this.API_URL_HEAD + 'submission', formData, httpOptions);
  }

  getSubmissionWithResults(submissionId:string): Observable<any>{
    return this.http.get(this.API_URL_HEAD + 'submission/' + submissionId);
  }

  deleteSubmission(submissionId:string): Observable<any>{
    return this.http.delete(this.API_URL_HEAD + 'submission/' + submissionId);
  }

  getPageSubmission(offset:number, paged:boolean, pageNumber:number, pageSize:number, sorted:boolean, unsorted:boolean, unpaged:boolean): Observable<any>{

    let httpOptions = {
      params: new HttpParams()
        .set('offset',""+offset)
        .set('paged', ""+paged)
        .set('pageNumber',""+pageNumber)
        .set('pageSize',""+pageSize)
        .set('sort.sorted',""+sorted)
        .set('sort.unsorted',""+unsorted)
        .set('unpaged', ""+unpaged)
    };

    return this.http.get(this.API_URL_HEAD + 'submission/page', httpOptions);
  }

  getAllSubmissions(problemId:string, contestId:string):Observable<any>{
    let httpOptions = {
      params: new HttpParams()
    };

    if(problemId!=null && problemId!=""){
      httpOptions.params.set("problemId", problemId);
    }
    if(contestId!=null && contestId!="") {
      httpOptions.params.set("contestId", contestId);
    }
      return this.http.get(this.API_URL_HEAD + 'submissions', httpOptions);


    }

  getAllSubmissionsMock(problemId:string, contestId:string):Observable<any>{
    let httpOptions = {
      params: new HttpParams()
    };

    if(problemId!=null && problemId!=""){
      httpOptions.params.set("problemId", problemId);
    }
    if(contestId!=null && contestId!="") {
      httpOptions.params.set("contestId", contestId);
    }
    return this.http.get('http://localhost:9897/apimock/' + 'submissions', httpOptions);


  }
}

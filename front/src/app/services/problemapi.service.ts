import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiCommonService} from './apicommon.service';

@Injectable({
  providedIn: 'root'
})
export class ProblemApiService extends ApiCommonService{

  constructor(private http: HttpClient) {
    super();
  }

  getAllProblems(): Observable<any>{
    return this.http.get(this.API_URL_HEAD + 'problem');
  }

/*  createProblemWithObject(){
    use the createProblemWithZip
  }
*/

  getSelectedProblem(problemId:string):Observable<any>{
    return this.http.get(this.API_URL_HEAD + 'problem/' + problemId);
  }

  updateProblem(problemId:string, pdf:File, problemName:string, teamId:string, timeout:string):Observable<any> {

    let httpOptions = {
      params: new HttpParams()
        .set('problemName', problemName)
        .set('teamId', teamId)
        .set('timeout', timeout)
    };

    if (pdf != null) {
      let formData:FormData = new FormData();
      formData.append('pdf', pdf, pdf.name);

      return this.http.put(this.API_URL_HEAD + 'problem/' + problemId, formData, httpOptions);
    }

    return this.http.put(this.API_URL_HEAD + 'problem/' + problemId, null, httpOptions);
  }

  deleteProblem(problemId:string){
    return this.http.delete(this.API_URL_HEAD + 'problem/' + problemId);
  }

  updateProblemFromZip(problemId:string, contestId:string, file:File, problemName:string, teamId:string): Observable<any>{

    let formData:FormData = new FormData();
    formData.append('file', file, file.name);

    let httpOptions = {
      params: new HttpParams()
        .set('problemName', problemName)
        .set('teamId', teamId)
        .set('contestId', contestId)

    };

    return this.http.put(this.API_URL_HEAD + 'problem/' + problemId + '/fromZip',formData, httpOptions);
  }

  getPdfFromProblem(problemId:string):Observable<any>{
    return this.http.get(this.API_URL_HEAD + 'problem/' + problemId + '/getPDF');
  }

  createProblemFromZip(contestId:string, file:File, problemName:string, teamId:string):Observable<any>{
    let formData:FormData = new FormData();
    formData.append('file', file, file.name);

    let httpOptions = {
      params: new HttpParams()
        .set('problemName', problemName)
        .set('teamId', teamId)
        .set('contestId', contestId)

    };

    return this.http.post(this.API_URL_HEAD + 'problem/fromZip',formData, httpOptions);
  }

  createSampleForProblem(problemId:string, inputFile:File, outputFile:File, isPublic:boolean, name:string):Observable<any>{
    let formData:FormData = new FormData();
    if(inputFile!= null && outputFile != null) {
      formData.append('entrada', inputFile, inputFile.name);
      formData.append('salida', outputFile, outputFile.name);
    }
    let httpOptions = {
      params: new HttpParams()
        .set('name', name)
        .set('isPublic', ''+isPublic)

    };

    return this.http.post(this.API_URL_HEAD + 'problem/' + problemId + '/sample/', formData, httpOptions);
  }

  updateSampleForProblem(problemId:string, sampleId:string, inputFile:File, outputFile:File, isPublic:boolean, name:string):Observable<any>{
    let formData:FormData = new FormData();
    if(inputFile!= null && outputFile != null) {
      formData.append('entrada', inputFile, inputFile.name);
      formData.append('salida', outputFile, outputFile.name);
    }
    let httpOptions = {
      params: new HttpParams()
        .set('name', name)
        .set('isPublic', ''+isPublic)
    };

    return this.http.put(this.API_URL_HEAD + 'problem/' + problemId + '/sample/' + sampleId, formData, httpOptions);
  }

  deleteSampleFromProblem(problemId:string, sampleId:string):Observable<any>{
    return this.http.delete(this.API_URL_HEAD + 'problem/' + problemId + '/sample/' + sampleId);
  }

}

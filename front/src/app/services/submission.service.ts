import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { SubmissionDTO } from '../dto/submission.dto';

const baseUrl = '/API/v1/submission';

@Injectable({
  providedIn: 'root'
})

export class SubmissionService {

  constructor(private http: HttpClient) { }

  getSubmissionWithResults(submissionId: string): Observable<SubmissionDTO> {
    return this.http.get<SubmissionDTO>(baseUrl + '/' + submissionId);
  }

  getAllSubmissions(problemId: string, contestId: string): Observable<SubmissionDTO[]> {
    let httpOptions = {
      params: new HttpParams()
    };

    if (problemId != null && problemId != "") {
      httpOptions.params.set("problemId", problemId);
    }
    if (contestId != null && contestId != "") {
      httpOptions.params.set("contestId", contestId);
    }
    return this.http.get<SubmissionDTO[]>(baseUrl + 's', httpOptions);
  }
  
//esto probablemente hay que cambiarlo
  getPageSubmission(offset: number, paged: boolean, pageNumber: number, pageSize: number, sorted: boolean, unsorted: boolean, unpaged: boolean): Observable<any> {

    let httpOptions = {
      params: new HttpParams()
        .set('offset', "" + offset)
        .set('paged', "" + paged)
        .set('pageNumber', "" + pageNumber)
        .set('pageSize', "" + pageSize)
        .set('sort.sorted', "" + sorted)
        .set('sort.unsorted', "" + unsorted)
        .set('unpaged', "" + unpaged)
    };

    return this.http.get(baseUrl + '/page', httpOptions);
  }

  createSubmission(codigo: File, contestId: string, lenguaje: string, problemId: string, teamId: string): Observable<SubmissionDTO> {
    return this.http.post<SubmissionDTO>(baseUrl, { codigo: codigo }, {
      params: {
        contestId: contestId,
        lenguaje: lenguaje,
        problemId: problemId,
        teamId: teamId,
      }
    });

  }

  deleteSubmission(submissionId: string): Observable<any> {
    return this.http.delete(baseUrl + '/' + submissionId);
  }
}

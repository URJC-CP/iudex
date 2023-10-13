import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProblemDto } from '../dto/problem.dto';
import { PageDTO } from '../dto/page.dto';

const baseUrl = '/API/v1/problem';

@Injectable({
  providedIn: 'root'
})

export class ProblemService {

  constructor(private http: HttpClient) { }

  getAllProblems(): Observable<ProblemDto[]> {
    return this.http.get<ProblemDto[]>(baseUrl);
  }

  getSelectedProblem(problemId: string): Observable<ProblemDto> {
    return this.http.get<ProblemDto>(baseUrl + '/' + problemId);
  }

  getPdfFromProblem(problemId: string): Observable<any> {
    return this.http.get(baseUrl + '/' + problemId + '/getPDF');
  }

  //esto probablemente hay que cambiarlo
  getPageProblem(offset: number, paged: boolean, pageNumber: number, pageSize: number, sorted: boolean, unsorted: boolean, unpaged: boolean): Observable<PageDTO<ProblemDto>> {
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
    return this.http.get<PageDTO<ProblemDto>>(baseUrl + '/page', httpOptions);
  }

  createProblemFromZip(contestId: string, file: File, problemName: string, teamId: string): Observable<ProblemDto> {
    let formData: FormData = new FormData();
    formData.append('file', file, file.name);

    return this.http.post<ProblemDto>(baseUrl + 'problem/fromZip', formData, {
      params: {
        problemName: problemName,
        teamId: teamId,
        contestId: contestId,
      }
    });
  }

  addSampleToProblem(problemId: string, inputFile: File, outputFile: File, isPublic: boolean, name: string): Observable<any> {
    let formData: FormData = new FormData();
    if (inputFile != null && outputFile != null) {
      formData.append('entrada', inputFile, inputFile.name);
      formData.append('salida', outputFile, outputFile.name);
    }

    return this.http.post<ProblemDto>(baseUrl + '/' + problemId + '/sample', formData, {
      params: {
        name: name,
        isPublic: isPublic,
      }
    });
  }

  updateProblem(problemId: string, pdf: File, problemName: string, teamId: string, timeout: string): Observable<ProblemDto> {
    if (pdf != null) {
      let formData: FormData = new FormData();
      formData.append('pdf', pdf, pdf.name);

      return this.http.put<ProblemDto>(baseUrl + '/' + problemId, formData, {
        params: {
          problemName: problemName,
          teamId: teamId,
          timeout: timeout,
        }
      });
    }

    return this.http.put<ProblemDto>(baseUrl + '/' + problemId, null, {
      params: {
        problemName: problemName,
        teamId: teamId,
        timeout: timeout,
      }
    });
  }

  updateSampleForProblem(problemId: string, sampleId: string, inputFile: File, outputFile: File, isPublic: boolean, name: string): Observable<any> {
    let formData: FormData = new FormData();
    if (inputFile != null && outputFile != null) {
      formData.append('entrada', inputFile, inputFile.name);
      formData.append('salida', outputFile, outputFile.name);
    }

    return this.http.put(baseUrl + '/' + problemId + '/sample/' + sampleId, formData, {
      params: {
        name: name,
        isPublic: isPublic,
      }
    });
  }

  updateProblemFromZip(problemId: string, contestId: string, file: File, problemName: string, teamId: string): Observable<ProblemDto> {
    let formData: FormData = new FormData();
    formData.append('file', file, file.name);

    return this.http.put<ProblemDto>(baseUrl + '/' + problemId + '/fromZip', formData, {
      params: {
        problemName: problemName,
        teamId: teamId,
        contestId: contestId,
      }
    });
  }

  deleteProblem(problemId: string) {
    return this.http.delete(baseUrl + '/' + problemId);
  }

  deleteSampleFromProblem(problemId: string, sampleId: string): Observable<any> {
    return this.http.delete(baseUrl + '/' + problemId + '/sample/' + sampleId);
  }

}

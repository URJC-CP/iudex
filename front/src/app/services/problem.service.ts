import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProblemDTO } from '../dto/problem.dto';
import { PageDTO } from '../dto/page.dto';

const baseUrl = '/API/v1/problem';

@Injectable({
  providedIn: 'root'
})

export class ProblemService {

  private problems: { [id: string]: Observable<ProblemDTO>; } = {};
  private allProblems: Observable<ProblemDTO[]>;

  constructor(private http: HttpClient) { }

  getAllProblems(): Observable<ProblemDTO[]> {
    if (this.allProblems) {
      return this.allProblems;
    } else {
      this.allProblems = this.http.get<ProblemDTO[]>(baseUrl);
      return this.allProblems;
    }
  }

  getSelectedProblem(problemId: string): Observable<ProblemDTO> {
    if (problemId in this.problems) {
      return this.problems[problemId];
    } else {
      this.problems[problemId] = this.http.get<ProblemDTO>(baseUrl + '/' + problemId);
      return this.problems[problemId];
    }
  }

  getPdfFromProblem(problemId: string): void {
    window.open(baseUrl + '/' + problemId + '/getPDF', '_blank');
  }

  //esto probablemente hay que cambiarlo
  getPageProblem(offset: number, paged: boolean, pageNumber: number, pageSize: number, sorted: boolean, unsorted: boolean, unpaged: boolean): Observable<PageDTO<ProblemDTO>> {
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
    return this.http.get<PageDTO<ProblemDTO>>(baseUrl + '/page', httpOptions);
  }

  createProblemFromZip(contestId: string, file: File, problemName: string, teamId: string): Observable<ProblemDTO> {
    let formData: FormData = new FormData();
    formData.append('file', file, file.name);

    return this.http.post<ProblemDTO>(baseUrl + 'problem/fromZip', formData, {
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

    return this.http.post<ProblemDTO>(baseUrl + '/' + problemId + '/sample', formData, {
      params: {
        name: name,
        isPublic: isPublic,
      }
    });
  }

  updateProblem(problemId: string, pdf: File, problemName: string, teamId: string, timeout: string): Observable<ProblemDTO> {
    if (pdf != null) {
      let formData: FormData = new FormData();
      formData.append('pdf', pdf, pdf.name);

      return this.http.put<ProblemDTO>(baseUrl + '/' + problemId, formData, {
        params: {
          problemName: problemName,
          teamId: teamId,
          timeout: timeout,
        }
      });
    }

    return this.http.put<ProblemDTO>(baseUrl + '/' + problemId, null, {
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

  updateProblemFromZip(problemId: string, contestId: string, file: File, problemName: string, teamId: string): Observable<ProblemDTO> {
    let formData: FormData = new FormData();
    formData.append('file', file, file.name);

    return this.http.put<ProblemDTO>(baseUrl + '/' + problemId + '/fromZip', formData, {
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

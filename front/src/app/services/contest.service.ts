import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ContestDTO } from '../dto/contest.dto';
import { PageDTO } from '../dto/page.dto';
import { ProblemScoreDTO } from '../dto/problemScore.dto';
import { TeamScoreDTO } from '../dto/teamScore.dto';
import { SubmissionDTO } from '../dto/submission.dto';
import { TeamDTO } from '../dto/team.dto';

const baseUrl = '/API/v1/contest';

@Injectable({
  providedIn: 'root'
})

export class ContestService {

  private contests: { [id: string]: Observable<ContestDTO>; } = {};
  private allContests: Observable<ContestDTO[]>;

  constructor(private http: HttpClient) {
  }

  getAllContests(): Observable<ContestDTO[]> {
    if (this.allContests) {
      return this.allContests;
    } else {
      this.allContests = this.http.get<ContestDTO[]>(baseUrl);
      return this.allContests;
    }
  }

  getSelectedContest(contestId: string): Observable<ContestDTO> {
    if (contestId in this.contests) {
      return this.contests[contestId];
    } else {
      this.contests[contestId] = this.http.get<ContestDTO>(baseUrl + '/' + contestId);
      return this.contests[contestId];
    }
  }

  //esto probablemente hay que cambiarlo
  getPageContest(offset: number, paged: boolean, pageNumber: number, pageSize: number, sorted: boolean, unsorted: boolean, unpaged: boolean): Observable<PageDTO<ContestDTO>> {
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
    return this.http.get<PageDTO<ContestDTO>>(baseUrl + '/page', httpOptions);
  }

  getScoreboard(contestId: string): Observable<TeamScoreDTO[]> {
    return this.http.get<TeamScoreDTO[]>(baseUrl + '/' + contestId + '/scoreboard');
  }

  getSubmissionsByContestAndTeam(contestId: string, teamId: string): Observable<SubmissionDTO[]> {
    return this.http.get<SubmissionDTO[]>(baseUrl + '/' + contestId + '/team/' + teamId + '/submissions');
  }

  createContest(contestName: string, descripcion: string, teamId: string, startTimestamp: number, endTimestamp: number): Observable<ContestDTO> {
    return this.http.post<ContestDTO>(baseUrl, {
      contestName: contestName,
      descripcion: descripcion,
      startTimestamp: startTimestamp,
      endTimestamp: endTimestamp,
      teamId: teamId,
    });
  }

  getCurrentTeam(contestId: string, userId: string): Observable<TeamDTO> {
    return this.http.get<TeamDTO>(baseUrl + '/' + contestId + '/user/' + userId + '/team');
  }

  addLanguageToContest(contestId: string, language: string): Observable<any> {
    return this.http.post(baseUrl + '/' + contestId + '/language', {
      language: language
    });
  }

  setAcceptedLanguagesContest(contestId: string, languagesList: string[]): Observable<any> {
    return this.http.post(baseUrl + '/' + contestId + '/language/addBulk', {
      languageList: languagesList.join(', '),
    });
  }

  updateContest(contestId: string, contestName: string, descripcion: string, teamId: string, startTimestamp: number, endTimestamp: number): Observable<ContestDTO> {
    return this.http.put<ContestDTO>(baseUrl + '/' + contestId, {
      contestName: contestName,
      descripcion: descripcion,
      startTimestamp: startTimestamp,
      endTimestamp: endTimestamp,
      teamId: teamId,
    });
  }

  addProblemToContest(problemId: string, contestId: string): Observable<any> {
    return this.http.put(baseUrl + '/' + contestId + '/' + problemId, null);
  }

  addTeamToContest(contestId: string, teamId: string): Observable<any> {
    return this.http.put(baseUrl + '/' + contestId + '/team/' + teamId, null);
  }

  bulkAddTeamsToContest(contestId: string, teamsList: string[]): Observable<any> {
    return this.http.put(baseUrl + '/' + contestId + '/team/addBulk', {
      teamList: teamsList
    });
  }

  deleteContest(contestId: string) {
    return this.http.delete(baseUrl + '/' + contestId);
  }

  deleteProblemFromContest(problemId: string, contestId: string) {
    return this.http.delete(baseUrl + '/' + contestId + '/' + problemId);
  }

  deleteTeamFromContest(contestId: string, teamId: string): Observable<any> {
    return this.http.delete(baseUrl + '/' + contestId + '/team/' + teamId);
  }

  bulkDeleteTeamFromContest(contestId: string, teamsList: string[]): Observable<any> {
    return this.http.delete(baseUrl + '/' + contestId + '/team/removeBulk', {
      params: {
        teamList: teamsList
      }
    });
  }

  deleteLanguageFromContest(contestId: string, langId: string): Observable<any> {
    return this.http.delete(baseUrl + '/' + contestId + '/language/' + langId);
  }


}
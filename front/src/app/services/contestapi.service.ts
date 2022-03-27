import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiCommonService} from './apicommon.service';

@Injectable({
  providedIn: 'root'
})
export class ContestApiService extends ApiCommonService{

  constructor(private http: HttpClient) {
    super();
  }

  getAllContests():Observable<any>{
    return this.http.get(this.API_URL_HEAD + 'contest');
  }

  createContest(contestName:string, descripcion:string, teamId:string, startTimestamp:number, endTimestamp:number): Observable<any>{
    let httpOptions = {
      params: new HttpParams()
        .set('contestName',contestName)
        .set('descripcion', descripcion)
        .set('startTimestamp',''+startTimestamp)
        .set('endTimestamp',''+endTimestamp)
        .set('teamId',teamId)
    };

    return this.http.post(this.API_URL_HEAD + 'contest', null, httpOptions);
  }

  getSelectedContest(contestId:string):Observable<any>{
    return this.http.get(this.API_URL_HEAD + 'contest/' + contestId);
  }

  updateContest(contestId:string, contestName:string, descripcion:string, teamId:string, startTimestamp:number, endTimestamp:number):Observable<any>{
    let httpOptions = {
      params: new HttpParams()
        .set('contestName',contestName)
        .set('descripcion', descripcion)
        .set('startTimestamp',''+startTimestamp)
        .set('endTimestamp',''+endTimestamp)
        .set('teamId',teamId)
    };
    return this.http.put(this.API_URL_HEAD + 'contest/' + contestId,null, httpOptions);
  }

  deleteContest(contestId:string){
    return this.http.delete(this.API_URL_HEAD + 'contest/' + contestId);
  }

  addProblemToContest(problemId:string, contestId:string):Observable<any>{
    let httpOptions = {
      params: new HttpParams()
    };
    return this.http.put(this.API_URL_HEAD + 'contest/' + contestId + '/' + problemId,null, httpOptions);
  }

  addLanguagesToContest(contestId:string, languagesList:string[]):Observable<any>{
    let httpOptions = {
      params: new HttpParams()
        .set('languageList', languagesList.join(', '))
    };

    return this.http.post(this.API_URL_HEAD + 'contest/' + contestId + '/language/addBulk',null, httpOptions);
  }

  deleteLanguageToContest(contestId:string, langId:string):Observable<any>{

    return this.http.delete(this.API_URL_HEAD + 'contest/' + contestId + '/language/' + langId);
  }

  addTeamsToContest(contestId:string, teamsList:string[]):Observable<any>{
    let httpOptions = {
      params: new HttpParams()
        .set('teamList', teamsList.join(', '))
    };

    return this.http.put(this.API_URL_HEAD + 'contest/' + contestId + '/team/addBulk',null, httpOptions);
  }

  deleteTeamsToContest(contestId:string, teamsList:string[]):Observable<any>{
    let httpOptions = {
      params: new HttpParams()
        .set('teamList', teamsList.join(', '))
    };

    return this.http.delete(this.API_URL_HEAD + 'contest/' + contestId + '/team/removeBulk',httpOptions);
  }
  getPageContest(offset:number, paged:boolean, pageNumber:number, pageSize:number, sorted:boolean, unsorted:boolean, unpaged:boolean):Observable<any>{

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
    return this.http.get(this.API_URL_HEAD + 'contest/page', httpOptions);
  }

  deleteProblemFromContest(problemId:string, contestId:string){
    return this.http.delete(this.API_URL_HEAD + 'contest/' + contestId + '/' + problemId);
  }

  getScoreboard(contestId:string):Observable<any>{
    return this.http.get(this.API_URL_HEAD + 'contest/' + contestId + '/scoreboard');
  }

  getScoreboardMock(contestId:string):Observable<any>{
    return this.http.get('http://localhost:9897/apimock/' + 'contest/' + contestId + '/scoreboard');
  }
}

import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiCommonService} from './apicommon.service';

@Injectable({
  providedIn: 'root'
})
export class TeamApiService extends ApiCommonService{

  constructor(private http: HttpClient) {
    super();
  }

  getAllTeams(): Observable<any>{
    return this.http.get(this.API_URL_HEAD + 'team');
  }

  createTeam(nombreEquipo:string): Observable<any>{

    let httpOptions = {
      params: new HttpParams()
        .set('nombreEquipo', nombreEquipo)
    };

    return this.http.post(this.API_URL_HEAD + 'team' , null, httpOptions);
  }

  getSelectedTeam(teamId:string):Observable<any>{
    return this.http.get(this.API_URL_HEAD + 'team/' + teamId);
  }

  updateTeam(teamId:string, teamName:string): Observable<any>{

    let httpOptions = {
      params: new HttpParams()
        .set('teamName', teamName)
    };

    return this.http.put(this.API_URL_HEAD + 'team/' + teamId,null, httpOptions);
  }

  deleteTeam(teamId:string){
    return this.http.delete(this.API_URL_HEAD + 'team/' + teamId);
  }

  addUserToTeam(teamId:string, userId:string): Observable<any>{
    return this.http.put(this.API_URL_HEAD + 'team/' + teamId + '/' + userId,null, null);
  }

  deleteUserFromTeam(teamId:string, userId:string){
    return this.http.delete(this.API_URL_HEAD + 'team/' + teamId + '/' + userId);
  }
}

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { TeamDTO } from '../dto/team.dto';

const baseUrl = '/API/v1/team';

@Injectable({
  providedIn: 'root'
})

export class TeamService {

  constructor(private http: HttpClient) { }

  getAllTeams(): Observable<TeamDTO[]> {
    return this.http.get<TeamDTO[]>(baseUrl);
  }

  getTeam(teamId: string): Observable<TeamDTO> {
    return this.http.get<TeamDTO>(baseUrl + '/' + teamId);
  }

  createTeam(nombreEquipo: string): Observable<TeamDTO> {
    return this.http.post<TeamDTO>(baseUrl, null, {
      params: {
        nombreEquipo: nombreEquipo,
      }
    });
  }

  updateTeam(teamId: string, teamName: string): Observable<any> {
    return this.http.put(baseUrl + '/' + teamId, null, {
      params: {
        teamName: teamName,
      }
    });
  }

  addUserToTeam(teamId: string, userId: string): Observable<TeamDTO> {
    return this.http.put<TeamDTO>(baseUrl + '/' + teamId + '/' + userId, null);
  }

  deleteTeam(teamId: string): Observable<any> {
    return this.http.delete(baseUrl + '/' + teamId);
  }

  deleteUserFromTeam(teamId: string, userId: string): Observable<any> {
    return this.http.delete(baseUrl + '/' + teamId + '/' + userId);
  }
}

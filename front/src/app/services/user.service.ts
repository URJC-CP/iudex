import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { UserDTO } from '../dto/user.dto';
import { ContestDTO } from '../dto/contest.dto';
import { empty } from 'rxjs';
import { OauthService } from "./oauth.service";

const baseUrl = '/API/v1/user';

@Injectable({
  providedIn: 'root'
})

export class UserService {

  private userContests: Observable<ContestDTO[]> | undefined;
  private user: Observable<UserDTO> | undefined;

  constructor(private http: HttpClient, private oauth: OauthService) { }

  getAllUsers(): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(baseUrl);
  }

  addRoleToUser(userId: string, role: string): Observable<string[]> {
    return this.http.post<string[]>(`${baseUrl}/${userId}/role/${role}`, null)
  }

  removeRoleFromUser(userId: string, role: string): Observable<string[]> {
    return this.http.delete<string[]>(`${baseUrl}/${userId}/role/${role}`);
  }

  getCurrentUser(): Observable<UserDTO> {
    if (this.user != undefined) {
      return this.user;
    } if (this.oauth.hasLoggedIn()) {
      this.user = this.http.get<UserDTO>(`${baseUrl}/me`);
      return this.user;
    } else {
      return new Observable<UserDTO>(subscriber =>
        subscriber.next({
          id: -1,
          nickname: "Anonymous",
          name: "Anonymous",
          email: "anonymous@urjc.es",
          roles: [],
          rolesString: "",
          submissions: 0,
          contestsParticipated: 0,
          acceptedSubmissions: 0,
        })
      );
    }
  }

  getUserContests(id: string): Observable<ContestDTO[]> {
    if (this.userContests != undefined) {
      return this.userContests;
    } else {
      this.userContests = this.http.get<ContestDTO[]>(`${baseUrl}/${id}/contests`);
      return this.userContests;
    }
  }

  logout() {
    this.userContests = undefined;
    this.user = undefined;
  }
}

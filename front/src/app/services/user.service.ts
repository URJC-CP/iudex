import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { UserDTO } from '../dto/user.dto';
import { ContestDTO } from '../dto/contest.dto';
import { empty } from 'rxjs';

const baseUrl = '/API/v1/user';

@Injectable({
  providedIn: 'root'
})

export class UserService {

  private userContests: Observable<ContestDTO[]> | undefined;
  private user: Observable<UserDTO> | undefined;

  constructor(private http: HttpClient) { }

  addRoleToUser(userId: string, role: string): Observable<string[]> {
    return this.http.post<string[]>(`${baseUrl}/${userId}/role/${role}`, null)
  }

  removeRoleFromUser(userId: string, role: string): Observable<string[]> {
    return this.http.delete<string[]>(`${baseUrl}/${userId}/role/${role}`);
  }

  getCurrentUser(): Observable<UserDTO> {
    if (this.user != undefined) {
      return this.user;
    } else {
      this.user = this.http.get<UserDTO>(`${baseUrl}/me`);
      return this.user;
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

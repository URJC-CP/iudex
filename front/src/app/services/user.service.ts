import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { UserDTO } from '../dto/user.dto';
import { ContestDTO } from '../dto/contest.dto';

const baseUrl = '/API/v1/user';

@Injectable({
  providedIn: 'root'
})

export class UserService {

  constructor(private http: HttpClient) { }

  addRoleToUser(userId: string, role: string): Observable<string[]> {
    return this.http.post<string[]>(`${baseUrl}/${userId}/role/${role}`, null)
  }

  removeRoleFromUser(userId: string, role: string): Observable<string[]> {
    return this.http.delete<string[]>(`${baseUrl}/${userId}/role/${role}`);
  }

  getCurrentUser(): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${baseUrl}/me`);
  }

  getUserContests(id: string): Observable<ContestDTO[]> {
    return this.http.get<ContestDTO[]>(`${baseUrl}/${id}/contests`);
  }
}

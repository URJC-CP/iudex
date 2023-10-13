import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';

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
}

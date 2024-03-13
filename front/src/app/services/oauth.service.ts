import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthResponseDTO } from '../dto/authResponse.dto';
import { ActivatedRoute, Route, Router } from '@angular/router';

const baseUrl = '/API/v1/oauth';

@Injectable({
  providedIn: 'root'
})

export class OauthService {

  constructor(private http: HttpClient, private route: ActivatedRoute, private router: Router) { }



  login(): void {
    // return this.http.get<AuthResponseDTO>(baseUrl);
    // Mirar si tiene sesión iniciada, si la tiene return
    window.location.href = baseUrl + '/login';
  }

  exchange(token: String): Observable<AuthResponseDTO> {
    return this.http.get<AuthResponseDTO>(baseUrl + '/exchange?token=' + token);
  }

  refresh(): Observable<AuthResponseDTO> {
    return this.http.get<AuthResponseDTO>(baseUrl + '/refresh');
  }

  saveTokens(authResponse: AuthResponseDTO): void {
    if(authResponse.accessToken && authResponse.refreshToken){
      localStorage.setItem('token', authResponse.accessToken);
      localStorage.setItem('refreshToken', authResponse.refreshToken);
    } else {
      localStorage.removeItem('token');
      localStorage.removeItem('refreshToken');
    }
  }

  getToken(){
    return localStorage.getItem('token');
  }

  getRefreshToken(){
    return localStorage.getItem('refreshToken');
  }

  hasLoggedIn(){
    return localStorage.getItem('token') != null;
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
  }
}

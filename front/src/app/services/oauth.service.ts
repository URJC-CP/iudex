import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import { AuthResponseDTO } from '../dto/authResponse.dto';

const baseUrl = '/API/v1/oauth/login';

@Injectable({
  providedIn: 'root'
})

export class OauthService {

  constructor(private http: HttpClient) { }

  login():Observable<AuthResponseDTO>{
    return this.http.get<AuthResponseDTO>(baseUrl);
  }
}

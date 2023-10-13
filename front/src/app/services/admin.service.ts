import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import { ResultDTO } from '../dto/result.dto';

const baseUrl = '/API/v1/result';

@Injectable({
  providedIn: 'root'
})

export class AdminService{

  constructor(private http: HttpClient) {
  }

  getAllResults(): Observable<ResultDTO>{
    return this.http.get<ResultDTO>(baseUrl + 'result');
  }

  getSelectedResult(resultId:string):Observable<ResultDTO>{
    return this.http.get<ResultDTO>(baseUrl + '/' + resultId);
  }
}
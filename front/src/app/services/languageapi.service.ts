import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiCommonService} from './apicommon.service';

@Injectable({
  providedIn: 'root'
})
export class LanguageApiService extends ApiCommonService{

  constructor(private http: HttpClient) {
    super();
  }

  getAllLanguages():Observable<any>{
    return this.http.get(this.API_URL_HEAD + 'language');
  }

}

import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import { LanguageDTO } from '../dto/language.dto';

const baseUrl = '/API/v1/language';

@Injectable({
  providedIn: 'root'
})

export class LanguageService {

  constructor(private http: HttpClient) { }

  getAllLanguages():Observable<LanguageDTO[]>{
    return this.http.get<LanguageDTO[]>(baseUrl);
  }

}

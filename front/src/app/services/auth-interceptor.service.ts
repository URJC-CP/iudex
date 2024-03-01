import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { OauthService } from './oauth.service';
import { of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptorService implements HttpInterceptor {

  constructor(private oauthService: OauthService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    //Mirar si hay token, si no si está en '/' nada y si está en otro lado redirigir a '/'
    const token: string = localStorage.getItem('token')!;
    let authReq = req;

    if (token) {
      authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    return next.handle(authReq).pipe(catchError<any, any>((error) => {
      if (error instanceof HttpErrorResponse && !authReq.url.includes('/login') && !authReq.url.includes('/exchange')
        && !authReq.url.includes('/refresh') && error.status === 401) {
        return this.refreshToken(authReq, next);
      }
      return new Error('unauthorized');
    })
    );
  }

  refreshToken(req: HttpRequest<any>, next: HttpHandler) {
    const refreshToken: string = localStorage.getItem('refreshToken')!;
    let authReq = req;
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${refreshToken}`
      }
    });
    if (refreshToken) {
      this.oauthService.refresh().subscribe((data) => {
        localStorage.setItem('token', data.accessToken);
        authReq = req.clone({
          setHeaders: {
            Authorization: 'Bearer' + data.accessToken
          }
        });
        return next.handle(authReq).pipe(catchError<any, any>((error) => {
          if (error instanceof HttpErrorResponse && error.status === 401) {
            localStorage.clear();
            window.location.href = '/';
          }
          console.log('error' + error);
          return new Error('unauthorized');
        }));
      });

    }

  }
}

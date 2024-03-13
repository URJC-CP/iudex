import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {catchError, from, Observable, switchMap, throwError} from 'rxjs';
import {OauthService} from './oauth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthInterceptorService implements HttpInterceptor {

  constructor(private oauthService: OauthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.oauthService.getToken();
    const refreshToken = this.oauthService.getRefreshToken();

    if (req.url.includes('/refresh') && refreshToken) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${refreshToken}`
        }
      });
    } else if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(req).pipe(catchError((error: HttpErrorResponse) => {
        if (error?.status === 401
          && !req.url.includes('/login')
          && !req.url.includes('/exchange')
          && !req.url.includes('/refresh')
        ) {
          return this.refreshToken(req, next);
        }
        return throwError(() => error);
      })
    );
  }

  refreshToken(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return from(this.oauthService.refresh()).pipe(
      switchMap((data) => {
        this.oauthService.saveTokens(data);
        req = req.clone({
          setHeaders: {
            Authorization: 'Bearer ' + data.accessToken
          }
        });
        return next.handle(req);
      }), catchError(error => {
        this.oauthService.logout(); // TODO review and prob change this, maybe leave as not logged in front page?
        return throwError(() => error);
      })
    );
  }
}

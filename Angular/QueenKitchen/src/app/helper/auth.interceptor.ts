import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HTTP_INTERCEPTORS } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenStorageService } from '../service/token-storage.service';
import { environment } from '../../environments/environment';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private tokens: TokenStorageService) {}
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.tokens.getToken();
    const api = environment.apiUrl + '/';
    // Only attach the JWT to our configured API, never to arbitrary remote URLs.
    if (!token || !req.url.startsWith(api)) return next.handle(req);
    return next.handle(req.clone({ setHeaders: { Authorization: 'Bearer ' + token } }));
  }
}
export const authInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
];

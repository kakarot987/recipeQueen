import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TokenStorageService } from '../service/token-storage.service';
import { environment } from '../../environments/environment';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private tokens: TokenStorageService) {}
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.tokens.getToken();
    // Never send credentials to unrelated domains or authentication endpoints.
    const allowed = req.url.startsWith(environment.apiUrl + '/');
    if (!token || !allowed) return next.handle(req);
    return next.handle(req.clone({ setHeaders: { Authorization: 'Bearer ' + token } }));
  }
}
export const authInterceptorProviders = [
  { provide: 'HTTP_INTERCEPTORS', useClass: AuthInterceptor, multi: true }
];

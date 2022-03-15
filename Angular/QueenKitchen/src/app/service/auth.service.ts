import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

const AUTH_API = 'http://localhost:5000/api/auth/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class AuthService {

  isLoggedInOk : boolean;

  constructor(private http: HttpClient) { 
    this.isLoggedInOk = false;
  }

  login(username:string,password:string): Observable<any> {
    return this.http.post(AUTH_API + 'signin', {
      username: username,
      password: password
    }, httpOptions
    );
  }
  register(username: string, email: string, password: string): Observable<any> {
    return this.http.post(AUTH_API + 'signup', {
      username,
      email,
      password,
      role:["user"]
    }, httpOptions);
  }

  //UploadProfilePic()
  getProfilePic(user_id :any) : Observable<any>{
    return this.http.post(AUTH_API + 'getUserProfilePic',{
      user_id
    },httpOptions);
  }

}
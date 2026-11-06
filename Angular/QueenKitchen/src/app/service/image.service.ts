import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest, HttpHeaders, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
const AUTH_API = 'http://localhost:5000/api/auth/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};
@Injectable({
  providedIn: 'root'
})
export class ImageService {

  constructor(private http: HttpClient) {}

  uploadProfilePic(image: File) : Observable<any>{
    const formData = new FormData();
    formData.append('file',image);
    return this.http.post(AUTH_API + 'uploadPic',{
      formData,
      user_id : 12
    },httpOptions);
  }
 
}
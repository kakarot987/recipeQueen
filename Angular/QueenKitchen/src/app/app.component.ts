import { Component} from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { TokenStorageService } from "./service/token-storage.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  isLoggedIn = false;
  constructor(private router: Router,private tokenStorage:TokenStorageService) {}
  ngOnInit(): void {
   // console.log(this.tokenStorage.getUser())
   // this.isLoggedIn = !!this.tokenStorage.getToken();
   if(this.tokenStorage.getToken() == '{}'){
    this.router.navigate(['/home']);
   }
   else{
    this.router.navigate(['/login']);
   }
  }

  logout(): void {
    this.tokenStorage.signOut();
  }
}
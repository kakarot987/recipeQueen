import { Component, OnInit ,ViewEncapsulation} from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AuthService } from '../service/auth.service';
import { TokenStorageService } from '../service/token-storage.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  encapsulation: ViewEncapsulation.None,
  styles: [`
    .dark-modal .modal-content {
      background-color: #292b2c;
      color: white;
    }
    .dark-modal .close {
      color: white;
    }
    .light-blue-backdrop {
      background-color: #5cb3fd;
    }
  `]
})
export class HomeComponent{

  title = 'QueenKitchen';
  closeResult !: string;
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = '';
  constructor(private modalService: NgbModal,private authService: AuthService,private tokenStorage: TokenStorageService) {
 
  }

  openLoginBackDropCustomClass(loginContent:any) {
    this.modalService.open(loginContent, {backdropClass: 'light-blue-backdrop'});
  }
  openSignUpBackDropCustomClass(signUpContent:any) {
    this.modalService.open(signUpContent, {backdropClass: 'light-blue-backdrop'});
  }

  //variables declaration for Login User
  role : any = []
  username !: string
  email !: string
  password !: string

  isUserRegistered = false
  userMessage : any

  isLoggedIn = false;
  isLoginFailed = false;
  errorMessageLogin = '';
  roles: string[] = [];

  onSubmit(data:any): void {
   this.username = data.username
   this.email = data.email
   this.password = data.password
  console.log(this.username,this.email,this.password)
    this.authService.register(this.username, this.email, this.password).subscribe(
        data => {
        console.log(data);
        this.isSuccessful = true;
        this.isSignUpFailed = false;
      },
      (err) => {
        this.errorMessage = err.error.message;
        this.isSignUpFailed = true;
      }
    );
  }
  onSubmitLogin(data:any):void{
    this.authService.login(data.loginUsername,data.loginPassword).subscribe(
      data =>{
        this.tokenStorage.saveToken(data.accessToken);
        this.tokenStorage.saveUser(data);
        this.isLoginFailed = false;
        this.isLoggedIn = true;
        this.roles = this.tokenStorage.getUser().roles;
        this.reloadPage();
      },
      err => {
        this.errorMessage = err.error.message;
        this.isLoginFailed = true;
      }
    );
  }

  reloadPage(): void {
    window.location.reload();
  }
}
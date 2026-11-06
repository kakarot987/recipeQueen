import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { TokenStorageService } from '../service/token-storage.service';

@Component({ standalone: false,
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  username = '';
  email = '';
  password = '';
  mode: 'login' | 'register' = 'login';
  busy = false;
  error = '';
  message = '';
  constructor(private auth: AuthService, private tokens: TokenStorageService, private router: Router) {}
  submit(): void {
    if (!this.username.trim() || !this.password || (this.mode === 'register' && !this.email.trim())) {
      this.error = 'Complete the required fields.'; return;
    }
    this.error = ''; this.message = ''; this.busy = true;
    if (this.mode === 'register') {
      this.auth.register(this.username, this.email, this.password).subscribe({
        next: () => { this.busy = false; this.message = 'Account created. Please sign in.'; this.mode = 'login'; this.password = ''; },
        error: err => { this.busy = false; this.error = err.error?.message || err.error?.error || 'Registration failed.'; }
      });
    } else {
      this.auth.login(this.username, this.password).subscribe({
        next: user => {
          this.tokens.saveToken(user.accessToken);
          this.tokens.saveUser(user);
          this.busy = false;
          this.router.navigate(['/login']);
        },
        error: () => { this.busy = false; this.error = 'Login failed. Check your credentials.'; }
      });
    }
  }
}

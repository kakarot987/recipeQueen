import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TokenStorageService } from './service/token-storage.service';

@Component({ selector: 'app-root', templateUrl: './app.component.html', styleUrls: ['./app.component.css'] })
export class AppComponent implements OnInit {
  constructor(private router: Router, private tokens: TokenStorageService) {}
  ngOnInit(): void {
    this.router.navigate([this.tokens.getToken() ? '/login' : '/home']);
  }
}

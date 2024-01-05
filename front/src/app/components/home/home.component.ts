import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthResponseDTO } from 'src/app/dto/authResponse.dto';
import { OauthService } from 'src/app/services/oauth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent {

  authresponse!: AuthResponseDTO;

  constructor(private router: Router, private oauthService: OauthService) {

  }

  login(){
    this.oauthService.login().subscribe((data) => {
      this.authresponse = data;
  });
  }

}

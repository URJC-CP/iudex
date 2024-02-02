import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Action } from 'rxjs/internal/scheduler/Action';
import { AuthResponseDTO } from 'src/app/dto/authResponse.dto';
import { OauthService } from 'src/app/services/oauth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent {

  authresponse!: AuthResponseDTO;

  constructor(private router: Router, private oauthService: OauthService, private activatedRoute: ActivatedRoute) {

  }

  login(){
    this.oauthService.login();
    // window.location.href = "/student"
  }

}

import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Action } from 'rxjs/internal/scheduler/Action';
import { AuthResponseDTO } from 'src/app/dto/authResponse.dto';
import { UserDTO } from 'src/app/dto/user.dto';
import { OauthService } from 'src/app/services/oauth.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent {

  authresponse!: AuthResponseDTO;
  private tempToken:string;
  private token: string;
  private user: UserDTO;

  constructor(private router: Router, private oauthService: OauthService, private userService: UserService, private activatedRoute: ActivatedRoute) {

  }

  ngOnInit(){
    if (this.router.url.includes('loginid')){
      this.tempToken =this.activatedRoute.snapshot.queryParamMap.get('loginid')!;
    console.log(this.tempToken);
    this.oauthService.exchange(this.tempToken).subscribe(data=> {this.token = data.accessToken?.tokenValue!; 
      console.log(data);}
    );
    localStorage.setItem('token', this.token);
    this.userService.getCurrentUser().subscribe(data => this.user = data);
    switch(this.user.role){
      case "student":
        window.location.href = "/student";
        break;
      case "judge":
        window.location.href = "/judge";
        break;
      case "admin":
        window.location.href = "/admin";
        break;
    }
    }

  }

  //TO-DO: control errores
  login(){
    this.oauthService.login();
  }

}

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
  private tempToken: string;
  private token: string;
  private user: UserDTO;

  constructor(private router: Router, private oauthService: OauthService, private userService: UserService, private activatedRoute: ActivatedRoute) {

  }

  ngOnInit() {
    if (this.router.url.includes('loginid')) {
      this.tempToken = this.activatedRoute.snapshot.queryParamMap.get('loginid')!;
      this.oauthService.exchange(this.tempToken).subscribe(data => {
        this.token = data.accessToken?.tokenValue!;
        localStorage.setItem('token', this.token);
        localStorage.setItem('refreshToken', data.refreshToken?.tokenValue!);
        this.userService.getCurrentUser().subscribe(me => {
          this.user = me
          console.log(this.user.roles);
          if (this.user.roles?.includes("ROLE_ADMIN")) {
            window.location.href = "/admin";
          } else if (this.user.roles?.includes("ROLE_JUDGE")) {
            window.location.href = "/judge";
          } else if (this.user.roles?.includes("ROLE_USER")) {
            window.location.href = "/student";
          }
        });
      }
      );
    }

  }

  //TO-DO: control errores
  login() {
    this.oauthService.login();
  }

}

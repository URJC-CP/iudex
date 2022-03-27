import { Component, ElementRef, ViewChild} from "@angular/core";
import {Router} from "@angular/router";
import {UserDto} from "../../dto/user.dto";
import {HttpClient} from "@angular/common/http";

@Component({
  templateUrl: './login.component.html'
})

export class LoginComponent {

  @ViewChild('emailInput') emailInput: ElementRef;
  @ViewChild('passwordInput') passwordInput: ElementRef;
  email:String;
  password:String;
  userDto:UserDto;
  constructor(private router:Router, private http: HttpClient) { }

  login(){

    this.email = this.emailInput.nativeElement.value;
    this.password = this.passwordInput.nativeElement.value;

    if(this.email.length && this.password.length > 0)  {
      this.http.get('http://localhost:9897/apimock/login/' + this.email).subscribe((response:UserDto) =>
        {
          this.userDto = response;
          console.log("UserDto mapped: {" + this.userDto.name + ', ' + this.userDto.surname + ", " + this.userDto.role + " }" );
          if(this.userDto.role == 'teacher'){
            this.router.navigate(['/teacher/menu']);
         //   alert('Welcome to teacher Menu ' + this.userDto.name + ' ' + this.userDto.surname);
          }else{
            this.router.navigate(['/student/menu']);
          }
        }
      );

    }else{
      alert($localize `:@@alertLoginKO:`);
    }

  };

}
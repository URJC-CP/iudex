import { Component } from '@angular/core';
import { NavigationStart, Router } from '@angular/router';
import { ThemeService } from 'src/app/services/theme.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent {

  val: boolean = false;
  onIcon = "pi pi-moon";
  onLabel = $localize`Dark Mode`;
  notHome: boolean;

  constructor(private router: Router, private themeService: ThemeService) {
    this.router.events.subscribe(
      (event) => {
        if (event instanceof NavigationStart) {
          if (event.url.endsWith("/")) {
            this.notHome = false;
          } else {
            this.notHome = true;
          }
        }
      }
    );
  }


  changeTheme() {
    if (this.val) {
      this.themeService.switchTheme("lightTheme");
    } else {
      this.themeService.switchTheme("darkTheme");
    }
  }

}

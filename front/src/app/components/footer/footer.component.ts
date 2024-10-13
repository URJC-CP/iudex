import { Component } from '@angular/core';
import { NavigationStart, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ThemeService } from 'src/app/services/theme.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})

export class FooterComponent {

  val: boolean = false;
  onIcon = "pi pi-moon";
  onLabel: string;
  notHome: boolean;
  currentLang: string;

  constructor(private router: Router, private themeService: ThemeService, public translate: TranslateService) {
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

  ngOnInit() {
    this.currentLang = this.translate.currentLang;

    this.translate.get('DarkMode').subscribe((res: string) => {
      this.onLabel = res;
    });
  }


  changeTheme() {
    if (this.val) {
      this.themeService.switchTheme("lightTheme");
    } else {
      this.themeService.switchTheme("darkTheme");
    }
  }

  changeLang(lang: string): void {
    this.translate.use(lang);
    localStorage.setItem('lang', lang);
    this.currentLang = lang;
  }

  getImage() {
    if (this.themeService.getTheme() == "darkTheme") {
      return "assets/images/logoURJCBlanco.png";
    } else {
      return "assets/images/logoURJC.png";
    }
  }

}

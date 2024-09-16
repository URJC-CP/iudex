import { Component } from '@angular/core';
import { NavigationSkipped, NavigationStart, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { MenuItem } from 'primeng/api';
import { firstValueFrom } from 'rxjs';
import { ContestService } from 'src/app/services/contest.service';
import { OauthService } from 'src/app/services/oauth.service';
import { ThemeService } from 'src/app/services/theme.service';
import { UserService } from 'src/app/services/user.service';

interface button {
  icon: string;
  name: string;
  url?: string;
}

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html'
})
export class NavbarComponent {

  notHome: boolean = true;
  loaded: boolean = true;
  items: MenuItem[] = [];
  buttons: button[] = [];
  pageType: string | undefined;
  userType: string | undefined;
  contestName: string | undefined;
  contestId: string | undefined;
  username: string | undefined;
  roles: string[] | undefined;
  currentTime: Date = new Date();
  contestEnd: Date = new Date();
  timeLeft: Date = new Date();
  negativeTime: boolean = false;
  days: number = 0;
  hours: number = 0;
  minutes: number = 0;
  seconds: number = 0;
  difference: number = 0;

  constructor(private router: Router, private contestService: ContestService, private userService: UserService, private oauthService: OauthService, private themeService: ThemeService, public translate: TranslateService) {
  }

  async ngOnInit() {
    this.router.events.subscribe(
      async (event) => {

        const translation = await firstValueFrom(this.translate.get('Name'));

        if (event instanceof NavigationStart) {
          this.buttons = [];
          this.items = [{
            label: this.translate.instant("Logout"),
            icon: 'pi pi-times',
            command: () => {
              this.logout();
            }
          },];
          if (!event.url.endsWith("/")) {
            // ASUMO QUE ADMINS SIEMPRE TIENEN TODOS LOS ROLES, Y QUE JUECES TIENEN SIEMPRE USER
            this.userService.getCurrentUser().subscribe((data) => {
              this.username = data.nickname!;
              this.roles = data.roles!;

              if (this.roles.includes("ROLE_ADMIN")) {
                this.userType = "admin";
              } else if (this.roles.includes("ROLE_JUDGE")) {
                this.userType = "judge";
              } else if (this.roles.includes("ROLE_USER")) {
                this.userType = "student";
              }
            });
          }
          if (event.url.endsWith("/student")) {
            this.loaded = false;
            this.pageType = "studentHome";
            this.studentHomeIcons();
          }
          else if (event.url.startsWith("/student") && !event.url.endsWith("/student")) {
            this.loaded = false;
            this.pageType = "student";
            this.contestId = event.url[17];
            this.contestService.getSelectedContest(this.contestId).subscribe((data) => {
              this.contestName = data.nombreContest!;
              this.contestEnd = new Date(data.endDateTime!);
              this.calculateTime();
              this.studentIcons();
            });
          }
          else if (event.url.startsWith("/judge")) {
            this.loaded = false;
            this.pageType = "judge";
            this.contestService.getAllContests().subscribe((data) => {
              if (data.length == 0) {
                this.contestId = "0";
              } else {
                this.contestId = String(data[0].id);
              }
              this.judgeIcons();
            });
          }
          else if (event.url.startsWith("/admin")) {
            this.pageType = "admin";
            this.adminIcons();
          } else if (event.url.endsWith("/")) {
            this.notHome = false;
          }
        }
      });
  }

  async studentHomeIcons() {
    while (!this.userType) {
      await new Promise(resolve => setTimeout(resolve, 100));
    }

    switch (this.userType) {
      case "admin":
        this.items.splice(0, 0, {
          label: this.translate.instant("AdminView"),
          icon: 'pi pi-user',
          routerLink: ['/admin']
        },
          {
            label: this.translate.instant("JudgeView"),
            icon: 'pi pi-user',
            routerLink: ['/judge']
          });
        break;
      case "judge":
        this.items.splice(0, 0, {
          label: this.translate.instant("JudgeView"),
          icon: 'pi pi-user',
          routerLink: ['/judge']
        })
    }
    this.loaded = true;
  }

  studentIcons() {
    this.buttons = [
      { icon: 'pi pi-fw pi-home', name: this.translate.instant("Home"), url: '/student/contest/' + this.contestId + '/home' },
      { icon: 'pi pi-fw pi-book', name: this.translate.instant("Problems"), url: '/student/contest/' + this.contestId + '/problems' },
      { icon: 'pi pi-fw pi-chart-bar', name: this.translate.instant("Ranking"), url: '/student/contest/' + this.contestId + '/ranking' }
    ];
    switch (this.userType) {
      case "admin":
        this.items.splice(0, 0, {
          label: this.translate.instant("AdminView"),
          icon: 'pi pi-user',
          routerLink: ['/admin']
        },
          {
            label: this.translate.instant("JudgeView"),
            icon: 'pi pi-user',
            routerLink: ['/judge']
          });
        break;
      case "judge":
        this.items.splice(0, 0, {
          label: this.translate.instant("JudgeView"),
          icon: 'pi pi-user',
          routerLink: ['/judge']
        })
    }
    if (this.buttons) {
      this.loaded = true;
    }
  }

  judgeIcons() {
    this.buttons = [
      { icon: 'pi pi-fw pi-star', name: this.translate.instant("Contests"), url: '/judge/contest' },
      { icon: 'pi pi-fw pi-book', name: this.translate.instant("Problems"), url: '/judge/problem' },
      { icon: 'pi pi-fw pi-code', name: this.translate.instant("Submissions"), url: '/judge/submission' },
      { icon: 'pi pi-fw pi-file', name: this.translate.instant("Ranking"), url: '/judge/ranking/' + this.contestId },
      // disabled?
      { icon: 'pi pi-fw pi-undo', name: this.translate.instant("Rejudge") }
    ];
    switch (this.userType) {
      case "admin":
        this.items.splice(0, 0, {
          label: this.translate.instant("AdminView"),
          icon: 'pi pi-user',
          routerLink: ['/admin']
        },
          {
            label: this.translate.instant("StudentView"),
            icon: 'pi pi-user',
            routerLink: ['/student']
          });
        break;
      case "judge":
        this.items.splice(0, 0, {
          label: this.translate.instant("StudentView"),
          icon: 'pi pi-user',
          routerLink: ['/student']
        })
    }
    if (this.buttons) {
      this.loaded = true;
    }
  }

  adminIcons() {
    this.buttons = [
      { icon: 'pi pi-fw pi-users', name: this.translate.instant("Users"), url: '/admin/user' },
      { icon: 'pi pi-fw pi-folder-open', name: this.translate.instant("Results"), url: '/admin/result' }
    ];
    this.items.splice(0, 0, {
      label: this.translate.instant("JudgeView"),
      icon: 'pi pi-user',
      routerLink: ['/judge']
    },
      {
        label: this.translate.instant("StudentView"),
        icon: 'pi pi-user',
        routerLink: ['/student']
      });
  }

  calculateTime() {
    setInterval(() => {
      this.getCurrentDate();
    }, 1000);
  }

  getCurrentDate() {
    this.currentTime = new Date();
    this.difference = this.contestEnd.getTime() - this.currentTime.getTime();

    this.negativeTime = this.difference < 0;

    const seconds = Math.floor((this.difference / 1000) % 60);
    const minutes = Math.floor((this.difference / 1000 / 60) % 60);
    const hours = Math.floor((this.difference / (1000 * 60 * 60)) % 24);
    const days = Math.floor(this.difference / (1000 * 60 * 60 * 24));

    this.days = Math.abs(days);
    this.hours = Math.abs(hours + days * 24);
    this.minutes = Math.abs(minutes);
    this.seconds = Math.abs(seconds);

  }

  logout() {
    this.oauthService.logout();
    this.userService.logout();
    window.location.href = "/";
  }

  getLogo() {
    if (this.themeService.getTheme() == "darkTheme") {
      return "assets/images/logo2.svg";
    } else {
      return "assets/images/logo2Black.svg";
    }
  }

}

import { Component } from '@angular/core';
import { NavigationSkipped, NavigationStart, Router } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { ContestService } from 'src/app/services/contest.service';
import { OauthService } from 'src/app/services/oauth.service';
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

  constructor(private router: Router, private contestService: ContestService, private userService: UserService, private oauthService: OauthService) {
  }

  ngOnInit() {

    this.router.events.subscribe(
      (event) => {
        if (event instanceof NavigationStart) {
          this.buttons = [];
          this.items = [{
            label: $localize`Log Out`,
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
              if (this.roles.includes("ROLE_ADMIN") && this.roles.length == 1) {
                this.userType = "admin";
              } else if (this.roles.includes("ROLE_JUDGE") && this.roles.length == 2) {
                this.userType = "judge";
              } else if (this.roles.includes("ROLE_USER") && this.roles.length == 3) {
                this.userType = "student";
              }
            });
          }
          if (event.url.endsWith("/student")) { this.pageType = "studentHome" }
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
            this.pageType = "judge";
            this.judgeIcons();
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

  studentIcons() {
    this.buttons = [
      { icon: 'pi pi-fw pi-home', name: $localize`Home`, url: '/student/contest/' + this.contestId + '/home' },
      { icon: 'pi pi-fw pi-book', name: $localize`Problems`, url: '/student/contest/' + this.contestId + '/problems' },
      { icon: 'pi pi-fw pi-chart-bar', name: $localize`Ranking`, url: '/student/contest/' + this.contestId + '/ranking' }
    ];
    switch (this.userType) {
      case "admin":
        this.items.splice(0, 0, {
          label: $localize`Admin View`,
          icon: 'pi pi-user',
          routerLink: ['/admin']
        },
          {
            label: $localize`Judge View`,
            icon: 'pi pi-user',
            routerLink: ['/judge']
          });
        break;
      case "judge":
        this.items.splice(0, 0, {
          label: $localize`Judge View`,
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
      { icon: 'pi pi-fw pi-star', name: $localize`Contests` },
      { icon: 'pi pi-fw pi-book', name: $localize`Problems` },
      { icon: 'pi pi-fw pi-code', name: $localize`Submissions` },
      { icon: 'pi pi-fw pi-file', name: $localize`Ranking` },
      // disabled?
      { icon: 'pi pi-fw pi-undo', name: $localize`Rejudge` }
    ];
    switch (this.userType) {
      case "admin":
        this.items.splice(0, 0, {
          label: $localize`Admin View`,
          icon: 'pi pi-user',
          routerLink: ['/admin']
        },
          {
            label: $localize`Student View`,
            icon: 'pi pi-user',
            routerLink: ['/student']
          });
        break;
      case "judge":
        this.items.splice(0, 0, {
          label: $localize`Student View`,
          icon: 'pi pi-user',
          routerLink: ['/student']
        })
    }
  }

  adminIcons() {
    this.buttons = [
      { icon: 'pi pi-fw pi-users', name: $localize`Users` },
      { icon: 'pi pi-fw pi-folder-open', name: $localize`Results` }
    ];
    if (this.userType == "admin") {
      this.items.splice(0, 0, {
        label: $localize`Judge View`,
        icon: 'pi pi-user',
        routerLink: ['/judge']
      },
        {
          label: $localize`Student View`,
          icon: 'pi pi-user',
          routerLink: ['/student']
        });
    }
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
    console.log(this.days + " " + this.hours + " " + this.minutes + " " + this.seconds);

  }

  logout() {
    this.oauthService.logout();
    this.userService.logout();
    window.location.href = "/";
  }


}

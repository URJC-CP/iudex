import { Component } from '@angular/core';
import { NavigationSkipped, NavigationStart, Router } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { ContestService } from 'src/app/services/contest.service';
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
  time: string | undefined;

  constructor(private router: Router, private contestService: ContestService, private userService: UserService) {
  }

  ngOnInit() {
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
          if (event.url.endsWith("/student")) { this.pageType = "studentHome" }
          if (event.url.startsWith("/student") && !event.url.endsWith("/student")) {
            this.loaded = false;
            this.pageType = "student";
            this.contestId = event.url[17];
            this.contestService.getSelectedContest(this.contestId).subscribe((data) => {
              this.contestName = data.nombreContest!;
              this.calculateTime();
              this.studentIcons();
            });
          }
          if (event.url.startsWith("/judge")) {
            this.pageType = "judge";
            this.judgeIcons();
          }
          if (event.url.startsWith("/admin")) {
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
          command: () => {
            this.logout();
          }
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
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    this.userService.logout();
    window.location.href = "/";
  }


}

import { Component } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { NavigationSkipped, NavigationStart, Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html'
})
export class NavbarComponent {
  items: MenuItem[] | undefined;
  studentItems: MenuItem[] | undefined;
  studentHomeItems: MenuItem[] | undefined;
  judgeItems: MenuItem[] | undefined;
  adminItems: MenuItem[] | undefined;
  username: MenuItem = {};
  pageType: string | undefined = "studentHome";
  userType: string | undefined = "student";

  constructor(private router: Router) {

    this.router.events.subscribe(
      (event) => {
        if (event instanceof NavigationStart) {
          if (event.url.endsWith("/student")) { this.pageType == "studentHome" }
          if (event.url.startsWith("/student") && !event.url.endsWith("/student")) { this.pageType == "student" }
          if (event.url.startsWith("/judge")) { this.pageType == "judge" }
          if (event.url.startsWith("/admin")) { this.pageType == "admin" }
        }
      });

    this.studentItems = [
      { label: $localize`Home`, icon: 'pi pi-fw pi-home' },
      { label: $localize`Problems`, icon: 'pi pi-fw pi-calendar' },
      { label: $localize`Ranking`, icon: 'pi pi-fw pi-pencil' },
      { label: $localize`Time`, style: { 'margin-left': 'auto' } },
      {
        label: $localize`Contest`, style: { 'margin-left': 'auto' }, command: () => {
          this.redirect("/student");
        }
      }
    ];

    this.judgeItems = [
      { label: $localize`Contests`, icon: 'pi pi-fw pi-home' },
      { label: $localize`Problems`, icon: 'pi pi-fw pi-calendar' },
      { label: $localize`Submissions`, icon: 'pi pi-fw pi-pencil' },
      { label: $localize`Ranking`, icon: 'pi pi-fw pi-file' },
      //disabled?
      { label: $localize`Rejudge`, icon: 'pi pi-fw pi-file' }
    ];

    this.adminItems = [
      { label: $localize`Users`, icon: 'pi pi-fw pi-home' },
      { label: $localize`Results`, icon: 'pi pi-fw pi-calendar' }
    ];

    this.studentHomeItems = [];

  }

  ngOnInit() {

    switch (this.userType) {
      case "student":
        this.username = {
          label: $localize`Username`, icon: 'pi pi-fw pi-user', style: { 'margin-left': 'auto' }, items: [
            {
              label: $localize`Log Out`,
              icon: 'pi pi-times',
              command: () => {
                this.logout();
              }
            }
          ]
        };
        break;
      case "judge":
        if (this.pageType = "student") {
          this.username = {
            label: $localize`Username`, icon: 'pi pi-fw pi-user', style: { 'margin-left': 'auto' }, items: [
              {
                label: $localize`Judge View`,
                icon: 'pi pi-refresh',
                routerLink: ['/judge']
              },
              {
                label: $localize`Log Out`,
                icon: 'pi pi-times',
                command: () => {
                  this.logout();
                }
              },
            ]
          }
        } else if (this.pageType = "judge") {
          this.username = {
            label: $localize`Username`, icon: 'pi pi-fw pi-user', style: { 'margin-left': 'auto' }, items: [
              {
                label: $localize`Student View`,
                icon: 'pi pi-refresh',
                routerLink: ['/student']
              },
              {
                label: $localize`Log Out`,
                icon: 'pi pi-times',
                command: () => {
                  this.logout();
                }
              },
            ]
          }
        }
        break;
      case "admin":
        if (this.pageType = "student") {
          this.username = {
            label: $localize`Username`, icon: 'pi pi-fw pi-user', style: { 'margin-left': 'auto' }, items: [
              {
                label: $localize`Admin View`,
                icon: 'pi pi-refresh',
                routerLink: ['/admin']
              },
              {
                label: $localize`Judge View`,
                icon: 'pi pi-refresh',
                routerLink: ['/judge']
              },
              {
                label: $localize`Log Out`,
                icon: 'pi pi-times',
                command: () => {
                  this.logout();
                }
              },
            ]
          }
        } else if (this.pageType = "judge") {
          this.username = {
            label: $localize`Username`, icon: 'pi pi-fw pi-user', style: { 'margin-left': 'auto' }, items: [
              {
                label: $localize`Admin View`,
                icon: 'pi pi-refresh',
                routerLink: ['/admin']
              },
              {
                label: $localize`Student View`,
                icon: 'pi pi-refresh',
                routerLink: ['/student']
              },
              {
                label: $localize`Log Out`,
                icon: 'pi pi-times',
                command: () => {
                  this.logout();
                }
              },
            ]
          }
        } else if (this.pageType = "admin") {
          this.username = {
            label: $localize`Username`, icon: 'pi pi-fw pi-user', style: { 'margin-left': 'auto' }, items: [
              {
                label: $localize`Judge View`,
                icon: 'pi pi-refresh',
                routerLink: ['/judge']
              },
              {
                label: $localize`Student View`,
                icon: 'pi pi-refresh',
                routerLink: ['/student']
              },
              {
                label: $localize`Log Out`,
                icon: 'pi pi-times',
                command: () => {
                  this.logout();
                }
              },
            ]
          }
        }
        break;
      default:
        break;
    }

    switch (this.pageType) {
      case "student":
        this.studentItems?.push(this.username);
        this.items = this.studentItems;
        break;
      case "judge":
        this.judgeItems?.push(this.username);
        this.items = this.judgeItems;
        break;
      case "admin":
        this.adminItems?.push(this.username);
        this.items = this.adminItems;
        break;
      case "studentHome":
        this.studentHomeItems?.push(this.username);
        this.items = this.studentHomeItems;
        break;
      default:
        break;
    }

  }

  logout() {

  }

  redirect(route: string) {

  }

}

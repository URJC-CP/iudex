import { Component } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html'
})
export class NavbarComponent {
  items: MenuItem[] | undefined;
  studentItems: MenuItem[] | undefined;
  judgeItems: MenuItem[] | undefined;
  adminItems: MenuItem[] | undefined;
  username: MenuItem = {};
  pageType: string | undefined;
  userType: string | undefined;

  constructor(private activatedRouter: ActivatedRoute, private router: Router) {
    this.activatedRouter.url.subscribe((data) => {
      this.pageType = data[1].path;
    });

    this.studentItems = [
      { label: 'Home', icon: 'pi pi-fw pi-home' },
      { label: 'Problems', icon: 'pi pi-fw pi-calendar' },
      { label: 'Ranking', icon: 'pi pi-fw pi-pencil' },
      { label: 'Time', icon: 'pi pi-fw pi-file' },
      { label: 'Contest', icon: 'pi pi-fw pi-cog', style: { 'margin-left': 'auto' } }
    ];

    this.judgeItems = [
      { label: 'Contests', icon: 'pi pi-fw pi-home' },
      { label: 'Problems', icon: 'pi pi-fw pi-calendar' },
      { label: 'Submissions', icon: 'pi pi-fw pi-pencil' },
      { label: 'Ranking', icon: 'pi pi-fw pi-file' },
      //disabled?
      { label: 'Rejudge', icon: 'pi pi-fw pi-file' }
    ];

    this.adminItems = [
      { label: 'Users', icon: 'pi pi-fw pi-home' },
      { label: 'Results', icon: 'pi pi-fw pi-calendar' }
    ];

  }

  ngOnInit() {

    switch (this.userType) {
      case "student":
        this.username = {
          label: 'Username', icon: 'pi pi-fw pi-user', style: { 'margin-left': 'auto' }, items: [
            {
              label: 'Log Out',
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
            label: 'Username', icon: 'pi pi-fw pi-user', style: { 'margin-left': 'auto' }, items: [
              {
                label: 'Judge View',
                icon: 'pi pi-refresh',
                routerLink: ['/judge']
              },
              {
                label: 'Log Out',
                icon: 'pi pi-times',
                command: () => {
                  this.logout();
                }
              },
            ]
          }
        } else if (this.pageType = "judge") {
          this.username = {
            label: 'Username', icon: 'pi pi-fw pi-user', style: { 'margin-left': 'auto' }, items: [
              {
                label: 'Student View',
                icon: 'pi pi-refresh',
                routerLink: ['/student']
              },
              {
                label: 'Log Out',
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
            label: 'Username', icon: 'pi pi-fw pi-user', style: { 'margin-left': 'auto' }, items: [
              {
                label: 'Admin View',
                icon: 'pi pi-refresh',
                routerLink: ['/admin']
              },
              {
                label: 'Judge View',
                icon: 'pi pi-refresh',
                routerLink: ['/judge']
              },
              {
                label: 'Log Out',
                icon: 'pi pi-times',
                command: () => {
                  this.logout();
                }
              },
            ]
          }
        } else if (this.pageType = "judge") {
          this.username = {
            label: 'Username', icon: 'pi pi-fw pi-user', style: { 'margin-left': 'auto' }, items: [
              {
                label: 'Admin View',
                icon: 'pi pi-refresh',
                routerLink: ['/admin']
              },
              {
                label: 'Student View',
                icon: 'pi pi-refresh',
                routerLink: ['/student']
              },
              {
                label: 'Log Out',
                icon: 'pi pi-times',
                command: () => {
                  this.logout();
                }
              },
            ]
          }
        } else if (this.pageType = "admin") {
          this.username = {
            label: 'Username', icon: 'pi pi-fw pi-user', style: { 'margin-left': 'auto' }, items: [
              {
                label: 'Judge View',
                icon: 'pi pi-refresh',
                routerLink: ['/judge']
              },
              {
                label: 'Student View',
                icon: 'pi pi-refresh',
                routerLink: ['/student']
              },
              {
                label: 'Log Out',
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
      default:
        break;
    }

  }

  logout() {

  }

}

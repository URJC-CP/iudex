import { Component } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { NavigationSkipped, NavigationStart, Router } from '@angular/router';
import { ContestService } from 'src/app/services/contest.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html'
})
export class NavbarComponent {
  contestName: string;
  contestId: string;
  subRouter: Subscription;
  subContest: Subscription;
  loaded: boolean = true;
  items: MenuItem[] = [];
  username: MenuItem = {};
  pageType: string | undefined;
  userType: string | undefined = "student";
  studentItems: MenuItem[] = [
    { label: $localize`Home`, icon: 'pi pi-fw pi-home', style: { 'margin-left': '4%' } },
    { label: $localize`Problems`, icon: 'pi pi-fw pi-book' },
    { label: $localize`Ranking`, style: { 'margin-right': 'auto' }, icon: 'pi pi-fw pi-chart-bar' },
    { label: $localize`Time?`, style: { 'margin': 'auto' } },
    { label: $localize`Contest`, style: { 'margin-left': 'auto' }, routerLink: ['/student'] }
  ];
  judgeItems: MenuItem[] = [
    { label: $localize`Contests`, icon: 'pi pi-fw pi-star' },
    { label: $localize`Problems`, icon: 'pi pi-fw pi-book' },
    { label: $localize`Submissions`, icon: 'pi pi-fw pi-code' },
    { label: $localize`Ranking`, icon: 'pi pi-fw pi-file' },
    //disabled?
    { label: $localize`Rejudge`, icon: 'pi pi-fw pi-undo' }
  ];
  adminItems: MenuItem[] = [
    { label: $localize`Users`, icon: 'pi pi-fw pi-users' },
    { label: $localize`Results`, icon: 'pi pi-fw pi-folder-open' }
  ];
  studentHomeItems: MenuItem[] = [];

  constructor(private router: Router, private contestService: ContestService) {
  }

  ngOnInit() {
    this.subRouter = this.router.events.subscribe(
      (event) => {
        if (event instanceof NavigationStart) {
          this.items = [];
          if (event.url.endsWith("/student")) { this.pageType = "studentHome" }
          if (event.url.startsWith("/student") && !event.url.endsWith("/student")) {
            this.loaded = false;
            this.pageType = "student";
            this.contestId = event.url[17];
            this.subRouter = this.contestService.getSelectedContest(this.contestId).subscribe((data) => {
              this.contestName = data.nombreContest!
              /* if (this.studentItems.length > 5) {
                this.studentItems.pop();
              }
              this.studentItems.pop(); */
              // this.studentItems = [...this.studentItems, { label: this.contestName, style: { 'margin-left': 'auto' }, routerLink: ['/student'] }];
              // this.studentItems.push({ label: this.contestName, style: { 'margin-left': 'auto' }, routerLink: ['/student'] });
              this.studentItems.splice(4, 1, { label: this.contestName, style: { 'margin-left': 'auto' }, routerLink: ['/student'] });
              this.loaded = true;
            });
          }
          if (event.url.startsWith("/judge")) { this.pageType = "judge" }
          if (event.url.startsWith("/admin")) { this.pageType = "admin" }
        }
        this.initUser();
        this.initItems();
      });
  }

  initUser() {
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
  }

  initItems() {
    if (this.items.length == 0) {
      switch (this.pageType) {
        case "student":
          if (this.studentItems.length == 5) {
            this.studentItems.push(this.username);
          } else {
            this.studentItems.pop();
            this.studentItems.push(this.username);
          }
          this.items = this.studentItems;
          break;
        case "judge":
          if (this.judgeItems.length == 5) {
            this.judgeItems.push(this.username);
          } else {
            this.judgeItems.pop();
            this.judgeItems.push(this.username);
          }
          this.items = this.judgeItems;
          break;
        case "admin":
          if (this.adminItems.length == 2) {
            this.adminItems.push(this.username);
          } else {
            this.adminItems.pop();
            this.adminItems.push(this.username);
          }
          this.items = this.adminItems;
          break;
        case "studentHome":
          this.studentHomeItems.pop()
          this.studentHomeItems.push(this.username);
          this.items = this.studentHomeItems;
          break;
        default:
          break;
      }
    }

  }

  logout() {

  }

  redirect(route: string) {

  }

  getMenuItem(array: MenuItem[], label: string): any {
    return array.find(item => item.label === label);
  }

  ngOnDestroy(){
    this.subContest.unsubscribe();
    this.subRouter.unsubscribe();
  }

}

import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import {Router} from "@angular/router";
import { Event as NavigationEvent } from "@angular/router";
import { filter } from "rxjs/operators";
import { NavigationStart } from "@angular/router";
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent{
  hiddenPpalMenu = false;

  constructor(private router: Router ) {
    router.events
      .pipe(
        filter(
          ( event: NavigationEvent ) => {
            return( event instanceof NavigationStart );
          }
        )
      )
      .subscribe(
        ( event: NavigationStart ) => {
          console.log( "trigger:", event.navigationTrigger );
          if(event.navigationTrigger == 'popstate'){
            this.hiddenPpalMenu = false;
          }
          console.groupEnd();
        });
  }

  onClick() {
    this.hiddenPpalMenu = true;
    this.router.navigate(['/auth/login']);
  }

  showPublicProblems(event) {
    this.hiddenPpalMenu = true;
   this.router.navigate(['/public/problems']);
  }

  showPublicSubmissions(event) {
    this.hiddenPpalMenu = true;

    this.router.navigate(['/public/submissions']);

  }

  showPublicRanking(event) {
    this.hiddenPpalMenu = true;

    this.router.navigate(['/public/ranking']);

  }

}

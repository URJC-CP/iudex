import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './components/home/home.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { FooterComponent } from './components/footer/footer.component';
import { StudentContestsComponent } from './components/student-contests/student-contests.component';

import { HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';
import { AuthInterceptorService } from './services/auth-interceptor.service';

import { routing } from './app.routing';

//PrimeNG imports
import { MenubarModule } from 'primeng/menubar';
import { MenuModule } from 'primeng/menu';
import { ButtonModule } from 'primeng/button';
import { SplitButtonModule } from 'primeng/splitbutton';
import { AccordionModule } from 'primeng/accordion';
import { CardModule } from 'primeng/card';
import { StudentHomeComponent } from './components/student-home/student-home.component';
import { StudentProblemsComponent } from './components/student-problems/student-problems.component';
import { StudentRankingComponent } from './components/student-ranking/student-ranking.component';
import { TableModule } from 'primeng/table';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    NavbarComponent,
    FooterComponent,
    StudentContestsComponent,
    StudentHomeComponent,
    StudentProblemsComponent,
    StudentRankingComponent
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    routing,
    MenubarModule,
    MenuModule,
    ButtonModule,
    SplitButtonModule,
    AccordionModule,
    CardModule,
    TableModule
  ],
  providers: [{
    provide: HTTP_INTERCEPTORS, useClass: AuthInterceptorService, multi: true
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }

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
import { StudentHomeComponent } from './components/student-home/student-home.component';
import { StudentProblemsComponent } from './components/student-problems/student-problems.component';
import { StudentRankingComponent } from './components/student-ranking/student-ranking.component';

import { HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';
import { AuthInterceptorService } from './services/auth-interceptor.service';

import { routing } from './app.routing';

import { MonacoEditorModule, NgxMonacoEditorConfig } from 'ngx-monaco-editor-v2';

import { ClipboardModule } from '@angular/cdk/clipboard';

//PrimeNG imports
import { MenubarModule } from 'primeng/menubar';
import { MenuModule } from 'primeng/menu';
import { ButtonModule } from 'primeng/button';
import { SplitButtonModule } from 'primeng/splitbutton';
import { AccordionModule } from 'primeng/accordion';
import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { SubmitButtonComponent } from './components/submit-button/submit-button.component';
import { FormsModule } from '@angular/forms';
import { SkeletonModule } from 'primeng/skeleton';
import { FileUploadModule } from 'primeng/fileupload';
import { ToastModule } from 'primeng/toast';
import { MessagesModule } from 'primeng/messages';
import { ToolbarModule } from 'primeng/toolbar';
import { TagModule } from 'primeng/tag';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { JudgeAdminTablesComponent } from './components/judge-admin-tables/judge-admin-tables.component';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http);
}

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    NavbarComponent,
    FooterComponent,
    StudentContestsComponent,
    StudentHomeComponent,
    StudentProblemsComponent,
    StudentRankingComponent,
    SubmitButtonComponent,
    NavbarComponent,
    JudgeAdminTablesComponent
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
    TableModule,
    DialogModule,
    DropdownModule,
    FormsModule,
    SkeletonModule,
    FileUploadModule,
    ToastModule,
    MessagesModule,
    ToolbarModule,
    TagModule,
    MonacoEditorModule.forRoot(),
    ClipboardModule,
    ToggleButtonModule,
    ConfirmDialogModule,
    TranslateModule.forRoot({
      defaultLanguage: 'es',
      loader: {
        provide: TranslateLoader,
        useFactory: (http: HttpClient) => new TranslateHttpLoader(http, './assets/i18n/', '.json'),
        deps: [HttpClient]
      }
    })
  ],
  providers: [{
    provide: HTTP_INTERCEPTORS, useClass: AuthInterceptorService, multi: true
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }

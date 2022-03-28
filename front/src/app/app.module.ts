import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {
  NbMenuModule,
  NbMenuService,
  NbThemeModule,
  NbThemeService,
  NbLayoutModule,
  NbButtonModule, NbAccordionModule, NbInputModule, NbCardModule, NbListModule, NbSelectModule, NbCheckboxModule
} from '@nebular/theme';
import { NbEvaIconsModule } from "@nebular/eva-icons";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {LoginComponent} from "./components/login/login.component";
import {MenuComponent} from "./components/menu/menu.component";
import { Ng2SmartTableModule } from 'ng2-smart-table';
import {FormsModule} from "@angular/forms";
import {SubmissionComponent} from "./components/submissions/submission.component";
import { MonacoEditorModule, MONACO_PATH } from '@materia-ui/ngx-monaco-editor';
import {ContestListComponent} from './components/menu/contestsList/contestList.component';
import {ProblemViewerComponent} from './components/problemViewer/problemviewer.component';
import {ProblemEditorComponent} from './components/problemEditor/problemeditor.component';
import {SubmissionViewerComponent} from './components/submissionViewer/submissionviewer.component';
import { PieChartComponent } from './components/pie-chart/pie-chart.component';
import {ChartsModule} from 'ng2-charts';
import {PublicProblemsComponent} from './components/allProblems/publicproblems.component';
import {PublicSubmissionsComponent} from './components/statsPublic/publicsubmissions.component';
import {RankingComponent} from './components/ranking/ranking.component';
import { IonicModule } from '@ionic/angular';
import {ContestCreatorComponent} from './components/menu/contestCreator/contestcreator.component';
import {ContestEditorComponent} from './components/menu/contestEditor/contesteditor.component';
import {TestCasesRemoverComponent} from './components/problemTestCases/delete/testcasesremover.component';
import {TestCasesAdderComponent} from './components/problemTestCases/add/testcasesadder.component';
import {TestCasesEditorComponent} from './components/problemTestCases/edit/testcaseseditor.component';


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    MenuComponent,
    SubmissionComponent,
    ContestListComponent,
    ProblemViewerComponent,
    ProblemEditorComponent,
    SubmissionViewerComponent,
    PieChartComponent,
    PublicProblemsComponent,
    PublicSubmissionsComponent,
    RankingComponent,
    ContestCreatorComponent,
    ContestEditorComponent,
    TestCasesRemoverComponent,
    TestCasesAdderComponent,
    TestCasesEditorComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    NbEvaIconsModule,
    NbButtonModule,
    NbAccordionModule,
    NbInputModule,
    NbThemeModule.forRoot(),
    NbMenuModule.forRoot(),
    BrowserAnimationsModule,
    //NbThemeModule.forRoot({name: 'dark'}),
    NbLayoutModule,
    NbCardModule,
    NbListModule,
    Ng2SmartTableModule,
    NbSelectModule,
    FormsModule,
    MonacoEditorModule,
    NbCheckboxModule,
    ChartsModule,
    IonicModule.forRoot()
  ],
  providers: [NbMenuService, NbThemeService,{
    provide: MONACO_PATH,
    useValue: 'https://unpkg.com/monaco-editor@0.21.2/min/vs'
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }

import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {LoginComponent} from "./components/login/login.component";
import {MenuComponent} from "./components/menu/menu.component";
import {SubmissionComponent} from "./components/submissions/submission.component";
import {ContestListComponent} from './components/menu/contestsList/contestList.component';
import {ProblemViewerComponent} from './components/problemViewer/problemviewer.component';
import {ProblemEditorComponent} from './components/problemEditor/problemeditor.component';
import {SubmissionViewerComponent} from './components/submissionViewer/submissionviewer.component';
import {PublicProblemsComponent} from './components/allProblems/publicproblems.component';
import {PublicSubmissionsComponent} from './components/statsPublic/publicsubmissions.component';
import {RankingComponent} from './components/ranking/ranking.component';
import {ContestCreatorComponent} from './components/menu/contestCreator/contestcreator.component';
import {ContestEditorComponent} from './components/menu/contestEditor/contesteditor.component';
import {TestCasesRemoverComponent} from './components/problemTestCases/delete/testcasesremover.component';
import {TestCasesAdderComponent} from './components/problemTestCases/add/testcasesadder.component';
import {TestCasesEditorComponent} from './components/problemTestCases/edit/testcaseseditor.component';

const routes: Routes = [

  {
    path: 'auth/login',
    component: LoginComponent,
  },

  {
    path: 'teacher/menu',
    component: ContestListComponent,
  },

  {
    path: 'student/menu',
    component: ContestListComponent,
  },

  {
    path: 'teacher/contest/:contestId',
    component: MenuComponent,
  },

  {
    path: 'student/contest/:contestId',
    component: MenuComponent,
  },

  {
    path: 'teacher/contest/:contestId/problem/:problemId/submission',
    component: SubmissionComponent,
  },

  {
    path: 'student/contest/:contestId/problem/:problemId/submission',
    component: SubmissionComponent,
  },

  {
    path: 'teacher/contest/:contestId/problem/:problemId',
    component: ProblemViewerComponent,
  },

  {
    path: 'teacher/contest/:contestId/problem/:problemId/edit',
    component: ProblemEditorComponent,
  },

  {
    path: 'teacher/contest/:contestId/problem/:problemId/samples/delete',
    component: TestCasesRemoverComponent,
  },

  {
    path: 'teacher/contest/:contestId/problem/:problemId/samples/add',
    component: TestCasesAdderComponent,
  },

  {
    path: 'teacher/contest/:contestId/problem/:problemId/samples/edit',
    component: TestCasesEditorComponent,
  },

  {
    path: 'student/contest/:contestId/problem/:problemId',
    component: ProblemViewerComponent,
    },

  {
    path: 'teacher/contest/:contestId/submission/:submissionId',
    component: SubmissionViewerComponent,
  },

  {
    path: 'teacher/createcontest',
    component: ContestCreatorComponent,
  },

  {
    path: 'teacher/editcontest',
    component: ContestEditorComponent,
  },

  {
    path: 'public/problems',
    component: PublicProblemsComponent,
  },

  {
    path: 'public/contest/:contestId/problem/:problemId',
    component: ProblemViewerComponent,
  },

  {
    path: 'public/submissions',
    component: PublicSubmissionsComponent,
  },

  {
    path: 'public/contest/:contestId/submission/:submissionId',
    component: SubmissionViewerComponent,
  },

  {
    path: 'public/ranking',
    component: RankingComponent,
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }

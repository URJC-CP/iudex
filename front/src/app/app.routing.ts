import { Routes, RouterModule } from '@angular/router';

import { HomeComponent } from './components/home/home.component';
import { StudentContestsComponent } from './components/student-contests/student-contests.component';
import { StudentProblemsComponent } from './components/student-problems/student-problems.component';
import { StudentRankingComponent } from './components/student-ranking/student-ranking.component';
import { StudentHomeComponent } from './components/student-home/student-home.component';

const appRoutes: Routes = [
  { path: "", component: HomeComponent },
  { path: "student", component: StudentContestsComponent },
  { path: "student/contest/:id/problems", component: StudentProblemsComponent },
  { path: "student/contest/:id/ranking", component: StudentRankingComponent },
  { path: "student/contest/:id/home", component: StudentHomeComponent },
]

  export const routing = RouterModule.forRoot(appRoutes);
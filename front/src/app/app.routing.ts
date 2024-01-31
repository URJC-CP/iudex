import { Routes, RouterModule } from '@angular/router';

import { HomeComponent } from './components/home/home.component';
import { StudentContestsComponent } from './components/student-contests/student-contests.component';
import { StudentProblemsComponent } from './components/student-problems/student-problems.component';

const appRoutes: Routes = [
  { path: "", component: HomeComponent },
  { path: "student", component: StudentContestsComponent },
  { path: "student/contest/:id", component: StudentProblemsComponent },
]

  export const routing = RouterModule.forRoot(appRoutes);
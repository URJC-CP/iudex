import { Routes, RouterModule } from '@angular/router';

import { HomeComponent } from './components/home/home.component';
import { StudentContestsComponent } from './components/student-contests/student-contests.component';

const appRoutes: Routes = [
  { path: "", component: HomeComponent },
  { path: "student", component: StudentContestsComponent },
]

  export const routing = RouterModule.forRoot(appRoutes);
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ContestDTO } from 'src/app/dto/contest.dto';
import { ContestService } from 'src/app/services/contest.service';
import { ProblemService } from 'src/app/services/problem.service';

@Component({
  selector: 'app-student-contests',
  templateUrl: './student-contests.component.html'
})
export class StudentContestsComponent {

  allContests: ContestDTO[] | undefined;

  constructor(private contestService: ContestService, private problemService: ProblemService, private router: Router) {

  }

  ngOnInit() {
    this.contestService.getAllContests().subscribe((contests) => {
      this.allContests = contests;
    });
  }

  redirect(id: string) {
    this.router.navigate(['/student/contest/' + id + '/problems']);
  }
}

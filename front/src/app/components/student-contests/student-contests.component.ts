import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ContestDTO } from 'src/app/dto/contest.dto';
import { ContestService } from 'src/app/services/contest.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-student-contests',
  templateUrl: './student-contests.component.html'
})
export class StudentContestsComponent {

  allContests: ContestDTO[] | undefined;
  totalSubmissions: number;
  contestsParticipated: number;
  acSubmissions: number;
  userId: string;


  constructor(private router: Router, private userService: UserService) {

  }

  ngOnInit() {
    this.userService.getCurrentUser().subscribe((data) => {
      this.totalSubmissions = data.submissions;
      this.contestsParticipated = data.contestsParticipated;
      this.acSubmissions = data.acceptedSubmissions;
      this.userId = String(data.id!);
      this.userService.getUserContests(this.userId).subscribe((contests) => {
        this.allContests = contests;
      });
    });
  }

  redirect(id: string) {
    this.router.navigate(['/student/contest/' + id + '/home']);
  }
}

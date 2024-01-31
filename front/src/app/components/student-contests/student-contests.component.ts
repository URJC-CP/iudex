import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ContestDTO } from 'src/app/dto/contest.dto';
import { ContestService } from 'src/app/services/contest.service';

@Component({
  selector: 'app-student-contests',
  templateUrl: './student-contests.component.html',
  styleUrls: ['../../../styles.css']
})
export class StudentContestsComponent {
  
  allContests: ContestDTO[] | undefined;
  forStart: number | undefined;
  forEnd: number | undefined;

  constructor(private contestService: ContestService, private router: Router) {

  }

  ngOnInit(){
    this.contestService.getAllContests().subscribe((contests) => {
      this.allContests = contests;
    });
  }

  redirect(id: string){
    this.router.navigate(['/student/contest/' + id]);
  }
}

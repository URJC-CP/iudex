import { Component } from '@angular/core';
import { ProblemDTO } from 'src/app/dto/problem.dto';
import { ProblemScoreDTO } from 'src/app/dto/problemScore.dto';
import { ContestService } from 'src/app/services/contest.service';

interface Column {
  field: string;
  header: string;
}

@Component({
  selector: 'app-student-ranking',
  templateUrl: './student-ranking.component.html'
})
export class StudentRankingComponent {

  scoreData!: ProblemScoreDTO[];
  id!: string;
  cols!: Column[];
  problems!: ProblemDTO[];

  constructor(private contestService: ContestService) {}

  ngOnInit() {
    this.contestService.getScoreboard(this.id).subscribe((data) => {
      this.scoreData = data;
  });
  this.contestService.getSelectedContest(this.id).subscribe((data) =>{
    this.problems = data.listaProblemas;
  });
  

      this.cols = [
          { field: 'position', header: 'Position' },
          { field: 'team', header: 'Team' },
          { field: 'score', header: 'Score' }
      ];

      this.problems.forEach(element => {
        this.cols.push({field:'scoreMap', header: element.nombreEjercicio});
      });
}
}
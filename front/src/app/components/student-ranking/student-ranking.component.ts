import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ProblemDTO } from 'src/app/dto/problem.dto';
import { ProblemScoreDTO } from 'src/app/dto/problemScore.dto';
import { TeamScoreDTO } from 'src/app/dto/teamScore.dto';
import { ContestService } from 'src/app/services/contest.service';

interface Column {
  field: string;
  header: string;
}

interface problems {
  score: string;
  tries: string;
  first: boolean;
  color: string
}

interface scores {
  position: string;
  team: string;
  score: string;
  problems: Map<String, problems>
}


@Component({
  selector: 'app-student-ranking',
  templateUrl: './student-ranking.component.html'
})
export class StudentRankingComponent {

  problemScore!: TeamScoreDTO[];
  scoreData: scores[] = [];
  id!: string;
  cols: Column[] = [];
  problems!: problems[];
  loaded: boolean;
  contestName!: string;
  end: number;

  constructor(private activatedRouter: ActivatedRoute, private contestService: ContestService) {
    activatedRouter.url.subscribe((data) => {
      this.id = data[2].path;
    });
  }

  ngOnInit() {

    this.loaded = false;

    this.contestService.getScoreboard(this.id).subscribe((data) => {
      this.problemScore = data;
      for (let i = 0; i < data.length; i++) {
        let map = new Map<String, problems>();
        data[i].scoreList.forEach((scoreProblem) => {
          let color = ""
          if (scoreProblem.first) {
            color = 'dark green'
          } else if (scoreProblem.score > 0){
            color = 'green'
          } else if (scoreProblem.tries == 0) {
            color = ''
          }else{
            color = 'red'
          }
          map.set(String(scoreProblem.problem.id), { score: String(scoreProblem.score), tries: String(scoreProblem.tries), first: scoreProblem.first, color: color})
        });
        this.scoreData.push({ position: String(i + 1), team: data[i].team?.nombreEquipo, score: String(data[i].score), problems: map })
        if (i == data.length - 1) {
        this.loaded = true;
        console.log(this.scoreData[0].problems.get('3')!.score)
        }
      }
    });
    this.contestService.getSelectedContest(this.id).subscribe((data1) => {
      this.contestName = data1.nombreContest;
      this.end = data1.listaProblemas.length;
      for (let i = 0; i < data1.listaProblemas.length; i++) {
        this.cols.push({ field: String(data1.listaProblemas[i].id), header: data1.listaProblemas[i].nombreEjercicio });
      }
    });

  }
}
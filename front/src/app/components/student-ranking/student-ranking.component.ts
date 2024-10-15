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
  solvedProblems: number;
  score: string;
  problems: Map<String, problems>
}

interface contest {
  name: string;
  id: string;
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
  pageType: string;
  selectedContest!: contest;
  contests: contest[] = [];
  hasScores: boolean = false;

  constructor(private activatedRouter: ActivatedRoute, private contestService: ContestService, private router: Router) {

    this.router.routeReuseStrategy.shouldReuseRoute = function () {
      return false;
    };

    activatedRouter.url.subscribe((data) => {
      this.id = data[2].path;

      if (data[0].path == "student") {
        this.pageType = "student";
      } else if (data[0].path == "judge") {
        this.pageType = "judge";
      }
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
            color = 'darkgreen'
          } else if (scoreProblem.solved) {
            color = 'green'
          } else {
            color = 'red'
          }
          map.set(String(scoreProblem.problem.id), { score: String(scoreProblem.score), tries: String(scoreProblem.tries), first: scoreProblem.first, color: color })
        });
        this.scoreData.push({ position: String(i + 1), team: data[i].team?.nombreEquipo, solvedProblems: data[i].solvedProblems, score: String(data[i].score), problems: map })
        if (i == data.length - 1) {
          this.loaded = true;
        }
      }
    });
    this.contestService.getSelectedContest(this.id).subscribe((data1) => {
      this.contestName = data1.nombreContest;
      this.selectedContest = { name: data1.nombreContest, id: String(data1.id) };
      this.end = data1.listaProblemas.length;
      this.hasScores = data1.listaProblemas.length > 0;
      for (let i = 0; i < data1.listaProblemas.length; i++) {
        this.cols.push({ field: String(data1.listaProblemas[i].id), header: data1.listaProblemas[i].nombreEjercicio });
      }
    });

    this.contestService.getAllContests().subscribe((data) => {
      for (let i = 0; i < data.length; i++) {
        this.contests = [...this.contests, { name: data[i].nombreContest, id: String(data[i].id) }];
      }
    });

  }

  changeContest() {
    if (this.selectedContest.id != this.id) {
      this.router.navigate(['/judge/ranking/' + this.selectedContest.id]);
    }
  }
}
import {Component, Input} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute, Router} from '@angular/router';
import {ContestApiDTO} from '../../dto/api.contest.dto';
import {ContestApiService} from '../../services/contestapi.service';
import {TeamScoreApiDTO} from '../../dto/api.teamscore.dto';
import {ProblemApiDto} from '../../dto/api.problem.dto';
import {logger} from 'codelyzer/util/logger';
import {SummaryRankingDto} from '../../dto/api.summary.ranking.dto';
import {SummaryProblemRankingDto} from '../../dto/api.summary.problem.ranking.dto';
import {ProblemScoreApiDTO} from '../../dto/api.problemscore.dto';

@Component({
  templateUrl: './ranking.component.html',
  selector: 'ranking-component'
})

export class RankingComponent {

  @Input() contestIdRanking:string; //id contest public por defecto
  teamScores:TeamScoreApiDTO[];
  problemsInContest:ProblemApiDto[];
  summaryRanking:SummaryRankingDto = new SummaryRankingDto();

  constructor(private activatedRoute: ActivatedRoute, private http: HttpClient, private router:Router,
              private contestApiService: ContestApiService) {
  }

  ngOnInit(){
    if(this.contestIdRanking == null || this.contestIdRanking == ""){
      this.contestIdRanking = "8"; //default public
    }

    this.contestApiService.getSelectedContest(this.contestIdRanking).subscribe(response => {
      let thisContest:ContestApiDTO = response;
      this.problemsInContest = thisContest.listaProblemas;
    });

    this.contestApiService.getScoreboard(this.contestIdRanking).subscribe(response => {
      this.teamScores = response;
      console.debug("response scoreboard" + this.teamScores);

      console.debug("create map with the ranking...");
      let previousScore : TeamScoreApiDTO = null;
      let position: number = 0;
      let allSolvedProblems: number = 0;
      let summaryProblem: SummaryProblemRankingDto = new SummaryProblemRankingDto();
      let summaryProblemMap:Map<number,SummaryProblemRankingDto> = new Map<number, SummaryProblemRankingDto>();
      let i = 0;
      this.teamScores.forEach(teamScore=>{
        //SUMMARY
        allSolvedProblems = allSolvedProblems + teamScore.solvedProblems;
        if (i==0) {
          //First iteration to generate the list with summary problems info
          teamScore.scoreList.forEach(scoreProblem => {
            this.fillSummaryMap(scoreProblem, summaryProblem);
            summaryProblemMap.set(scoreProblem.problem.id, summaryProblem);
            summaryProblem = new SummaryProblemRankingDto();
          })
        }else {
          teamScore.scoreList.forEach(scoreProblem => {
            summaryProblem = summaryProblemMap.get(scoreProblem.problem.id);
            this.fillSummaryMap(scoreProblem, summaryProblem);
            summaryProblemMap.set(scoreProblem.problem.id, summaryProblem);
            summaryProblem = new SummaryProblemRankingDto();
          })
        }
        if((position != 0) && (previousScore.solvedProblems == teamScore.solvedProblems) && (previousScore.score == teamScore.score)){
          //puntuacion igual --> comparten posicion
          teamScore.rankingPosition = position;
        }else{
          // distinta puntuacion --> puesto inferior (se supone ordenado)
          position = position + 1;
          teamScore.rankingPosition = position;
          previousScore = teamScore;
        }
        i = i + 1;
      });
      this.summaryRanking.score = allSolvedProblems;
      this.summaryRanking.summaryProblemList = Array.from(summaryProblemMap.values());
      logger.debug("ranking map finished!")
    });
  }

  fillSummaryMap(scoreProblem: ProblemScoreApiDTO, summaryProblem: SummaryProblemRankingDto) {
    if (scoreProblem.score == 0) {
      summaryProblem.allKOs = summaryProblem.allKOs + scoreProblem.tries;
    } else if (scoreProblem.tries > 0) {
      summaryProblem.allKOs = summaryProblem.allKOs + (scoreProblem.tries - 1);
    }
    if (scoreProblem.first) {
      summaryProblem.bestTime = scoreProblem.score;
    }
    if(scoreProblem.score > 0){
      summaryProblem.allOKs = summaryProblem.allOKs + 1;
    }
  }

  selectColorToCell(first:boolean, score:number, tries:number){
    if(first){
      //verde oscuro
      return {'background-color': 'rgba(26,122,15,0.6)'};
    }
    if(score>0){
      //verde
      return {'background-color': 'rgba(0,255,0,0.3)'};

    }else if (score==0 && tries >0){
      //rojo
      return {'background-color': 'rgba(255,0,0,0.3)'};
    }
  }

  checkIfIsUndoned(score:number, tries:number) {
    return score==0 && tries==0;
  }

}

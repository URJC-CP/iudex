import {TeamApiDTO} from './api.team.dto';

import {ProblemScoreApiDTO} from './api.problemscore.dto';

export interface TeamScoreApiDTO {

  score?:number;
  scoreList?:ProblemScoreApiDTO[];
  solvedProblems?:number;
  team?:TeamApiDTO;
  rankingPosition?:number;
}


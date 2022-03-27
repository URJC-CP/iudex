import {ProblemApiDto} from './api.problem.dto';

export interface ProblemScoreApiDTO {

  first?:boolean;
  problem?:ProblemApiDto;
  score?:number;
  tries?:number;
  timestamp?: number;
}


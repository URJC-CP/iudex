import { ContestApiDTO } from './api.contest.dto';
import {SubmissionApiDTO} from './api.submission.dto';
import {ProblemApiDto} from './api.problem.dto';
import {UserApiDTO} from './api.userapi.dto';

export interface TeamApiDTO {
  id?: number;
  nombreEquipo?: string;
  listaContestsCreados?: ContestApiDTO[];
  listaContestsParticipados?: ContestApiDTO[];
  listaDeSubmissions?: SubmissionApiDTO[];
  listaProblemasCreados?: ProblemApiDto[];
  listaProblemasParticipados?: ProblemApiDto[];
  participantes?: UserApiDTO[];
  timestamp?: number;

}


import { ContestDTO } from './contest.dto';
import {SubmissionDTO} from './submission.dto';
import {ProblemDto} from './problem.dto';
import {UserDTO} from './user.dto';

export interface TeamDTO {
  id?: number;
  nombreEquipo?: string;
  listaContestsCreados?: ContestDTO[];
  listaContestsParticipados?: ContestDTO[];
  listaDeSubmissions?: SubmissionDTO[];
  listaProblemasCreados?: ProblemDto[];
  listaProblemasParticipados?: ProblemDto[];
  participantes?: UserDTO[];
  timestamp?: number;

}
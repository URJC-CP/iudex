import { ContestDTO } from './contest.dto';
import {SubmissionDTO} from './submission.dto';
import {ProblemDTO} from './problem.dto';
import {UserDTO} from './user.dto';

export interface TeamDTO {
  id?: number;
  nombreEquipo?: string;
  listaContestsCreados?: ContestDTO[];
  listaContestsParticipados?: ContestDTO[];
  listaDeSubmissions?: SubmissionDTO[];
  listaProblemasCreados?: ProblemDTO[];
  listaProblemasParticipados?: ProblemDTO[];
  participantes?: UserDTO[];
  timestamp?: number;

}
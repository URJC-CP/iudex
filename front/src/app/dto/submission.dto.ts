import {TeamDTO} from './team.dto';
import {LanguageDTO} from './language.dto';
import {ResultDTO} from './result.dto';
import {ProblemBasicInfoDto} from './problembasicinfo.dto';

export interface SubmissionDTO {
  id?: number;
  results?: ResultDTO[];
  team?: TeamDTO;
  problem?: ProblemBasicInfoDto;
  corregido?: boolean;
  numeroResultCorregidos?: number;
  resultado?: string;
  language?:LanguageDTO;
  execSubmissionTime?: number;
  execSubmissionMemory?: number;
  timestamp?: number;

}
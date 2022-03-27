import {TeamApiDTO} from './api.team.dto';
import {LanguageApiDTO} from './api.language.apidto';
import {ResultApiDTO} from './api.resultapi.dto';
import {ApiProblemBasicInfoDto} from './api.problembasicinfo.dto';

export interface SubmissionApiDTO {
  id?: number;
  results?: ResultApiDTO[];
  team?: TeamApiDTO;
  problem?: ApiProblemBasicInfoDto;
  corregido?: boolean;
  numeroResultCorregidos?: number;
  resultado?: string;
  language?:LanguageApiDTO;
  execSubmissionTime?: number;
  execSubmissionMemory?: number;
  timestamp?: number;

}


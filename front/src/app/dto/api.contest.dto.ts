import {TeamApiDTO} from './api.team.dto';

import {ProblemApiDto} from './api.problem.dto';
import {LanguageDTO} from './api.language.dto';

export interface ContestApiDTO {
  descripcion?: string;
  id?: string;
  listaParticipantes?: TeamApiDTO[];
  listaProblemas?:ProblemApiDto[];
  nombreContest?: string;
  teamPropietario?: TeamApiDTO;
  timestamp?: number;
  endDateTime?: number;
  startDateTime?: number;
  lenguajesAceptados?: LanguageDTO[];
}


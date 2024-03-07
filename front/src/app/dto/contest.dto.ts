import { TeamDTO } from './team.dto';

import { ProblemDTO } from './problem.dto';
import { LanguageDTO } from './language.dto';

export interface ContestDTO {
  descripcion?: string;
  id: string;
  listaParticipantes?: TeamDTO[];
  listaProblemas: ProblemDTO[];
  nombreContest: string;
  teamPropietario?: TeamDTO;
  timestamp?: number;
  endDateTime?: number;
  startDateTime?: number;
  lenguajesAceptados: LanguageDTO[];
}
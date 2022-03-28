import {ProblemDataDTO} from './api.problemdata.dto';
import {LanguageDTO} from './api.language.dto';

export interface ResultDto {
  codigo?: string;
  entrada?: string;
  entradaInO ?: ProblemDataDTO;
  execMemory ?: number;
  execTime ?: number;
  fileName ?: string;
  id ?: number;
  language ?: LanguageDTO;
  maxMemory ?: string;
  maxTimeout ?: string;
  numeroCasoDePrueba ?: number;
  resultadoRevision ?: string;
  revisado ?: boolean;
  salidaCompilador ?: string;
  salidaError ?: string;
  salidaEstandar ?: string;
  salidaEstandarCorrecta ?: string;
  salidaEstandarCorrectaInO ?: ProblemDataDTO;
  salidaTime ?: string;
  signalCompilador ?: string;
  signalEjecutor ?: string;
  timestamp ?: number;

}


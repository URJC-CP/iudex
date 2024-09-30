import { LanguageDTO } from "./language.dto";
import { SampleDTO } from "./sample.dto";
import { SubmissionDTO } from "./submission.dto";

export interface ResultDTO {
  id: number;
  codigo?: string;
  sample?: SampleDTO;
  salidaEstandar?: string;
  salidaError?: string;
  salidaCompilador?: string;
  timestamp: number;
  numeroCasoDePrueba?: number;
  salidaTime?: string;
  signalCompilador?: string;
  signalEjecutor?: string;
  execTime?: number;
  execMemory?: number;
  revisado?: boolean;
  resultadoRevision: string;
  language: LanguageDTO;
  fileName?: string;
  maxMemory?: string;
  maxTimeout?: string;
  entrada?: string;
  salidaEstandarCorrecta?: string;

}

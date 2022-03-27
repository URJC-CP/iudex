
import {LanguageApiDTO} from './api.language.apidto';

export interface ResultApiDTO {
  codigo?: string;
  execMemory ?: number;
  execTime ?: number;
  id ?: number;
  language ?: LanguageApiDTO;
  numeroCasoDePrueba ?: number;
  resultadoRevision ?: string;
  revisado ?: boolean;
  salidaTime ?: string;
  timestamp ?: number;

}


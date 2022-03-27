import {TeamApiDTO} from './api.team.dto';
import {SamplesApiDTO} from './api.samples.dto';
import {SubmissionApiDTO} from './api.submission.dto';


export interface ProblemApiDto {

  id?: number;
  nombreEjercicio?: string;
  samples?: SamplesApiDTO[];
  submissions?: SubmissionApiDTO[];
  equipoPropietario?: TeamApiDTO;
  valido?:boolean;
  timeout?:string;
  memoryLimit?:String;
  color?:String;
  problemURLpdf?:String;

}

import { TeamDTO } from './team.dto';
import { SampleDTO } from './sample.dto';
import { SubmissionDTO } from './submission.dto';

export interface ProblemDTO {
  id: number;
  nombreEjercicio: string;
  samples: SampleDTO[];
  submissions: SubmissionDTO[];
  equipoPropietario?: TeamDTO;
  valido?: boolean;
  timeout: string;
  memoryLimit: string;
  color: string;
  problemURLpdf?: string;

}
import { ProblemDTO } from "./problem.dto";

export interface ProblemScoreDTO {
    problem: ProblemDTO;
    first: boolean;
    score: number;
    tries: number;
    timestamp?: number;
}
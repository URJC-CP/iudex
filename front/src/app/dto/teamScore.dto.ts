import { TeamDTO } from "./team.dto";
import { ProblemDTO } from "./problem.dto";
import { ProblemScoreDTO } from "./problemScore.dto";

export interface TeamScoreDTO {
    team?: TeamDTO;
    scoreMap?: Map<ProblemDTO, ProblemScoreDTO>;
    score?: number;
    solvedProblems?: number;
}
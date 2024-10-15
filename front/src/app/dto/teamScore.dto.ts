import { TeamDTO } from "./team.dto";
import { ProblemDTO } from "./problem.dto";
import { ProblemScoreDTO } from "./problemScore.dto";

export interface TeamScoreDTO {
    team: TeamDTO;
    scoreList: ProblemScoreDTO[];
    score: number;
    solvedProblems: number;
}
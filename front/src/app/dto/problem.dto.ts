export class ProblemDto {

  reference?: String;
  pdf?: String;
  difficulty?: String;
  testCases?: String;
  title?: String;

  constructor(){
    this.reference = "";
    this.pdf = "";
    this.difficulty = "";
    this.testCases = "";
    this.title = "";
  }
}

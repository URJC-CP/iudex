export class SubmissionDto {

  submissionId?:String;
  problemRef?:String;
  alumn?: String;
  date?: String;
  result?: String;

  constructor(){
    this.submissionId = "";
    this.problemRef = "";
    this.alumn = "";
    this.date = "";
    this.result = "";
  }

}

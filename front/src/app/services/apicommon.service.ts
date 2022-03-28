
export class ApiCommonService{

  API_URL_HEAD: string;

  constructor() {
    // TODO: FIX this, should be in the environment, and use relative paths
    this.API_URL_HEAD = 'http://localhost:8080/API/v1/';
  }

}

import { Component} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';
import {ContestApiDTO} from '../../../dto/api.contest.dto';
import {ContestApiService} from '../../../services/contestapi.service';
import * as moment from 'moment';
import {LanguageDTO} from '../../../dto/api.language.dto';
import {LanguageApiService} from '../../../services/languageapi.service';
import {TeamApiDTO} from '../../../dto/api.team.dto';
import {TeamApiService} from '../../../services/teamapi.service';


@Component({
  templateUrl: './contesteditor.component.html',
})

export class ContestEditorComponent {
  contestId:string;
  contestName:string;
  contestDescription:string;
  startDateContest:any;
  endDateContest:any;
  constestList:ContestApiDTO[];
  allLanguagesList:LanguageDTO[];
  languageListWithoutExisting:string[];
  currentLanguagesList:LanguageDTO[];
  languagesToAdd:string[] = [];
  languagesToDelete:string[] = [];

  allTeams:TeamApiDTO[];
  teamsListNotInCurrentContest:TeamApiDTO[];
  teamsInCurrentContest:TeamApiDTO[];
  teamsToAdd:string[] = [];
  teamsToDelete:string[] = [];

  hiddenEditMainInfo:boolean = true;
  hiddenAddLang:boolean = true;
  hiddenAddTeams:boolean = true;

  constructor(private activatedRoute: ActivatedRoute, private http: HttpClient, private router:Router, private contestService:ContestApiService,
              private langService:LanguageApiService, private teamsService:TeamApiService) {
    this.contestService.getAllContests().subscribe(data =>{
        this.constestList = data;
    });

    this.langService.getAllLanguages().subscribe(data =>{
      this.allLanguagesList = data;
    })

    this.teamsService.getAllTeams().subscribe(data=>{
      this.allTeams = data;
    })

  }

  onChangeSelect(value) {
    let contestSelected:ContestApiDTO;
    contestSelected = value;
    this.languageListWithoutExisting = [];
    this.teamsListNotInCurrentContest = [];
    let langNamesAtThisContest = []
    let teamIdsAtThisContest =[];

    //main
    let startDate = new Date(contestSelected.startDateTime);
    let endDate = new Date(contestSelected.endDateTime);

    this.contestId = contestSelected.id;
    this.contestName = contestSelected.nombreContest;
    this.contestDescription =contestSelected.descripcion;
    this.startDateContest = moment(startDate).format("YYYY-MM-DDTkk:mm")
    this.endDateContest = moment(endDate).format("YYYY-MM-DDTkk:mm");

    //languages
    this.currentLanguagesList = contestSelected.lenguajesAceptados;
    this.currentLanguagesList.forEach(l=>{
      langNamesAtThisContest.push(l.nombreLenguaje);
    })
    this.allLanguagesList.forEach(lang=>{
      if(!langNamesAtThisContest.includes(lang.nombreLenguaje)){
        this.languageListWithoutExisting.push(lang.nombreLenguaje);
      }
    });

    //teams
    this.teamsInCurrentContest = contestSelected.listaParticipantes;
    this.teamsInCurrentContest.forEach(t=>{
      teamIdsAtThisContest.push(t.id);
    });
    this.allTeams.forEach(team=>{
      if(!teamIdsAtThisContest.includes(team.id)){
        this.teamsListNotInCurrentContest.push(team);
      }
    })

  }



  save(){
    if (!this.hiddenEditMainInfo){
      let startDate = new Date(this.startDateContest);
      let endDate = new Date(this.endDateContest);
      this.contestService.updateContest(this.contestId, this.contestName, this.contestDescription, "7",startDate.getTime(), endDate.getTime()).subscribe(data=>{
        this.router.navigate(['/teacher/menu']);
      }, error => {
        alert("API ERROR " + error.status);
      })
    }else if(!this.hiddenAddLang){
      if(this.languagesToAdd.length != 0){
        this.contestService.addLanguagesToContest(this.contestId,this.languagesToAdd).subscribe(data=>{
          this.router.navigate(['/teacher/menu']);
        },error => {
          alert("API ERROR " + error.status);
        });
      }

      if(this.languagesToDelete.length != 0){
        this.languagesToDelete.forEach(langId=>{
          this.contestService.deleteLanguageToContest(this.contestId, langId).subscribe(data =>{
             this.router.navigate(['/teacher/menu']);
          });
        })
      }

    }else if(!this.hiddenAddTeams){
      if(this.teamsToAdd.length != 0){
        this.contestService.addTeamsToContest(this.contestId,this.teamsToAdd).subscribe(data=>{
          this.router.navigate(['/teacher/menu']);
        },error => {
          alert("API ERROR " + error.status);
        });
      }

      if(this.teamsToDelete.length != 0){
          this.contestService.deleteTeamsToContest(this.contestId, this.teamsToDelete).subscribe(data =>{
            this.router.navigate(['/teacher/menu']);
          },error => {
            alert("API ERROR " + error.status);
         });
      }
    }
  }

  onChangeSelectInfoToEdit(value) {
    this.hiddenEditMainInfo = !(value=="mainInfo");
    this.hiddenAddLang = !(value=="addLang");
    this.hiddenAddTeams = !(value=="addTeams");
  }

  onChangeSelectLanguagesAdd(value) {
    this.languagesToAdd = value;
  }

  onChangeSelectLanguagesDelete(value) {
    this.languagesToDelete = value;
  }

  onChangeSelectTeamsAdd(value){
    this.teamsToAdd = value;
  }
  onChangeSelectTeamsDelete(value){
    this.teamsToDelete = value;
  }
}

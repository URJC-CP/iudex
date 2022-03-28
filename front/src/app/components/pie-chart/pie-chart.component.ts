import {Component, Input, OnInit} from '@angular/core';
@Component({
  selector: 'app-pie-chart',
  templateUrl: './pie-chart.component.html'
})
export class PieChartComponent implements OnInit {

  @Input() pieChartLabels:string[];
  @Input() pieChartData:number[];
  pieChartType = 'pie';
  pieChartColours = [{
    backgroundColor:['rgba(0,255,0,0.3)', 'rgba(255,0,0,0.3)', 'rgba(175,175,175,0.3)']
  }];

  constructor() {
  }

  ngOnInit() {
  }

}

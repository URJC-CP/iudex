import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { PrimeNGConfig } from 'primeng/api';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['../styles.css']
})
export class AppComponent {
  title = 'IUDEX';

  constructor(private primengConfig: PrimeNGConfig, private translateService: TranslateService) {
    this.translateService.use(localStorage.getItem("lang") || 'es');
    localStorage.setItem('lang', this.translateService.currentLang);
  }

  ngOnInit() {
    this.primengConfig.ripple = true;
  }

  translate(lang: string) {
    this.translateService.use(lang);
    localStorage.setItem('lang', lang);
    this.translateService.get('primeng').subscribe(res => this.primengConfig.setTranslation(res));
  }

}

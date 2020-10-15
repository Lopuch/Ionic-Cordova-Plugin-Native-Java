import {Component} from '@angular/core';
import {MyZebraTest} from '../../providers/myZebraTest/myZebraTest';


@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage {
  result;
  error;

  constructor(
    private mujZebraTest: MyZebraTest,
    ) {
  }

  async onMyZebraTestClick(){

    try {
      this.result = await this.mujZebraTest.coolMethod({
        objednavkaC: 'nula0',
        mnozstvi: 'jedna1',
        vyrobek: 'dva2',
        name: 'tri3',
        kontrola: 'ctyri4',
        charge: 'pet5',
        datum: 'sest6'
      });
      console.log('Vysledek zebry: ', this.result);
    } catch (e) {
      this.error = e;
    }
  }
}

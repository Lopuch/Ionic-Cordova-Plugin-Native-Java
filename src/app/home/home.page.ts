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
      this.result = await this.mujZebraTest.coolMethod('klobasa');
      console.log('Vysledek zebry: ', this.result);
    } catch (e) {
      this.error = e;
    }
  }
}

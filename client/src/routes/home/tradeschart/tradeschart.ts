import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { chartsGradients } from '@genesislcap/g2plot-chart';
import { Store } from '../../../store';
import { tradesChartTemplate } from './tradeschart.template';

@customElement({
  name: 'trades-chart',
template: tradesChartTemplate,
})
export class TradesChart extends FASTElement {
  @Store store: Store;

  @observable lineChartConfiguration = {
    padding: 'auto',
    seriesField: 'series',
    xField: 'groupBy',
    yField: 'value',
    xAxis: {
      type: 'time',
      tickCount: 10,
    },
    color: [chartsGradients.rapidGreen, chartsGradients.rapidRed],
  };
}

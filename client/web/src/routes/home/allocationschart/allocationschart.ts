import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { Store } from '../../../store';
import { allocationsChartTemplate } from './allocationschart.template';

@customElement({
  name: 'allocations-chart',
  template: allocationsChartTemplate,
})
export class AllocationsChart extends FASTElement {
  @Store store: Store;
  @observable donutChartsConfiguration = {
    angleField: 'value',
    colorField: 'groupBy',
    label: {
      type: 'spider',
      labelHeight: 28,
      content: '{name}\n{percentage}',
      style: {
        fill: 'white',
      },
    },
    interactions: [{ type: 'element-selected' }, { type: 'element-active' }],
  };
}

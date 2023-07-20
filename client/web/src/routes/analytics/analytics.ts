import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { AnalyticsTemplate as template } from './analytics.template';
import { AnalyticsStyles as styles } from './analytics.styles';

const name = 'reports-route';

@customElement({
  name,
  template,
  styles,
})
export class Analytics extends FASTElement {
  @observable chartsConfiguration = {
    width: 600,
    angleField: 'value',
    colorField: 'groupBy',
    radius: 0.75,
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

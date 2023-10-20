import { html } from '@microsoft/fast-element';
import { TradesChart } from './tradeschart';

export const tradesChartTemplate = html<TradesChart>`
  <template>
    <zero-card class="chart-card">
      <zero-g2plot-chart type="line" :config=${(x) => x.lineChartConfiguration}>
        <chart-datasource
          resourceName="ALL_TRADES"
          server-fields="TRADE_DATETIME PRICE SIDE"
          chart-fields="groupBy value side"
          isSnapshot="false"
          criteria=${(x) => x.store.lineChartFilterCriteria}
        ></chart-datasource>
      </zero-g2plot-chart>
    </zero-card>
  </template>
`;

import { html } from '@microsoft/fast-element';
import { AllocationsChart } from './allocationschart';

export const allocationsChartTemplate = html<AllocationsChart>`
  <template>
    <zero-card class="chart-card">
      <zero-g2plot-chart type="donut" :config=${(x) => x.donutChartsConfiguration}>
        <chart-datasource
          resourceName="ALL_CHART_DATA"
          server-fields="SIDE INSTRUMENT_SIDE_ALLOCATION"
          charts-fields="groupBy value"
          isSnapshot="false"
          criteria=${(x) => x.store.pieChartFilterCriteria}
        ></chart-datasource>
      </zero-g2plot-chart>
    </zero-card>
  </template>
`;

import { html } from '@microsoft/fast-element';
import type { Analytics } from './analytics';

export const AnalyticsTemplate = html<Analytics>`
  <div class="split-layout">
    <zero-g2plot-chart type="pie" :config=${(x) => x.chartsConfiguration}>
      <chart-datasource
        resourceName="ALL_POSITIONS"
        server-fields="INSTRUMENT_NAME VALUE"
        charts-fields="groupBy value"
        isSnapshot
      ></chart-datasource>
    </zero-g2plot-chart>
  </div>
`;

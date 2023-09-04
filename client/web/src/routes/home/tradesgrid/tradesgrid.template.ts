import { html, ref, repeat } from '@microsoft/fast-element';
import { tradeColumnDefs } from '../tradeColumnDefs';
import { tradesGridStyles } from '../trades-grid.styles';
import { TradesGrid } from './tradesgrid';

export const tradesGridTemplate = html<TradesGrid>`
  <template ${ref('tradesGridTemplate')}>
    <zero-card class="trades-card">
      <zero-filter-bar
        resource="ALL_TRADES"
        only="INSTRUMENT_NAME, SIDE, QUANTITY, CURRENCY, PRICE, CONSIDERATION, TRADE_DATETIME, COUNTERPARTY_NAME, TRADE_STATUS, ENTERED_BY"
        columns="10"
        labels="Instrument, Side, Quantity, Ccy, Price, Consideration, Date, Counterparty, Trade State, Entered By"
        target="tradesDatasource"
      ></zero-filter-bar>
      <zero-grid-pro rowHeight="45" only-template-col-defs ${ref('tradeGrid')} enabledRowFlashing>
        <grid-pro-genesis-datasource
          resource-name="ALL_TRADES"
          order-by="TRADE_DATETIME"
          criteria=${(x) => x.store.tradesFilterCriteria}
          ${ref('tradesDatasource')}
        ></grid-pro-genesis-datasource>
        <slotted-styles :styles=${() => tradesGridStyles}></slotted-styles>
        ${repeat(
          () => tradeColumnDefs,
          html`
            <grid-pro-column :definition="${(x) => x}" />
          `
        )}
        <grid-pro-column :definition="${(x) => x.singleTradeActionColDef}" />
      </zero-grid-pro>
    </zero-card>
  </template>
`;

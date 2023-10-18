import { html, ref, repeat } from '@microsoft/fast-element';
import { positionColumnDefs } from '../positionColumnDefs';
import { PositionsGrid } from './positionsgrid';

export const positionsGridTemplate = html<PositionsGrid>`
  <template ${ref('positionsGridTemplate')}>
    <zero-card class="positions-card">
      <zero-filter-bar
        resource="ALL_POSITIONS"
        only="INSTRUMENT_NAME, QUANTITY, CURRENCY, NOTIONAL, VALUE, PNL"
        columns="6"
        labels="Instrument, Quantity, Ccy, Traded Value, Market Value, PNL"
        target="positionsDatasource"
      ></zero-filter-bar>
      <zero-grid-pro rowHeight="45" only-template-col-defs ${ref('positionsGrid')}>
        <grid-pro-genesis-datasource
          resource-name="ALL_POSITIONS"
          order-by="INSTRUMENT_ID"
          :deferredGridOptions=${(x) => x.positionsGridOptions}
          criteria=${(x) => x.store.positionsFilterCriteria}
          ${ref('positionsDatasource')}
        ></grid-pro-genesis-datasource>
        ${repeat(
          () => positionColumnDefs,
          html`
            <grid-pro-column :definition="${(x) => x}" />
          `,
        )}
        <grid-pro-column :definition="${(x) => x.singlePositionActionColDef}" />
      </zero-grid-pro>
    </zero-card>
  </template>
`;

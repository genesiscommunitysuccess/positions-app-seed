import { html, ref, when } from '@microsoft/fast-element';
import type { Home } from './home';
import { addTradeFormSchema } from './addTradeFormSchema';
import { TradesChart } from './tradeschart';
import { AllocationsChart } from './allocationschart';
import { TradesGrid } from './tradesgrid';
import { PositionsGrid } from './positionsgrid';
import { sync } from '@genesislcap/foundation-utils';
import { CriteriaSegmentedControlOption, Serialisers } from '@genesislcap/foundation-criteria';
import { allPositionsFilterSchema } from './tradeFilterSchema';

PositionsGrid;
TradesGrid;
AllocationsChart;
TradesChart;

const toolbarOptions: CriteriaSegmentedControlOption[] = [
  { label: 'VOD', field: 'INSTRUMENT_ID', value: 'VOD', serialiser: Serialisers.EQ },
  { label: 'LSEG', field: 'INSTRUMENT_ID', value: 'LSEG', serialiser: Serialisers.EQ },
  { label: 'LLOY', field: 'INSTRUMENT_ID', value: 'LLOY', serialiser: Serialisers.EQ },
  { label: 'BP', field: 'INSTRUMENT_ID', value: 'BP', serialiser: Serialisers.EQ },
  { label: 'AZN', field: 'INSTRUMENT_ID', value: 'AZN', serialiser: Serialisers.EQ },
];

export const HomeTemplate = html<Home>`
  <template>
    <zero-modal
      ${ref('addTradeModal')}
      position="right"
      :onCloseCallback=${(x) => x.closeNavCallback.bind(x)}
    >
      ${when(
        (x) => !x.store.sideNavClosed,
        html<Home>`
          <foundation-form
            style="height: 100%;"
            resourceName=${() => 'EVENT_TRADE_INSERT'}
            :data=${(x) => x.store.addTradeData}
            :uischema=${() => addTradeFormSchema}
            @submit-success=${(x) => x.closeNavCallback()}
          ></foundation-form>
        `
      )}
    </zero-modal>

    <zero-card class="filter-card">
      <criteria-segmented-control
        :criteriaOptions=${() => toolbarOptions}
        :value=${sync((x) => x.segmentedInstrumentValue)}
      >
        <h4 slot="label" class="filters-label">Trades Filter</h4>
      </criteria-segmented-control>
      <h4 class="filters-label">Positions Filter</h4>
      <foundation-filters
        style="height: fit-content;"
        :value=${sync((x) => x.tradesCriteria)}
        :uischema=${() => allPositionsFilterSchema}
        resourceName="ALL_POSITIONS"
      ></foundation-filters>
    </zero-card>
    <zero-layout auto-save-key="position-app-seed-layout" ${ref('layout')}>
      <zero-layout-region type="vertical">
        <zero-layout-region type="horizontal">
          <zero-layout-item title="Positions Table">
            <positions-grid></positions-grid>
          </zero-layout-item>
          <zero-layout-item title="Allocations Chart">
            <allocations-chart></allocations-chart>
          </zero-layout-item>
        </zero-layout-region>
        <zero-layout-region type="horizontal">
          <zero-layout-item title="Trades Table">
            <trades-grid></trades-grid>
          </zero-layout-item>
          <zero-layout-item title="Trades Chart">
            <trades-chart></trades-chart>
          </zero-layout-item>
        </zero-layout-region>
      </zero-layout-region>
    </zero-layout>
  </template>
`;

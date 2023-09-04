import { GridProGenesisDatasource, ZeroGridPro } from '@genesislcap/foundation-zero-grid-pro';
import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { Events, ColDef } from '@ag-grid-community/core';
import { Store, StoreEventDetailMap, TRADE_EVENTS } from '../../../store';
import { Connect } from '@genesislcap/foundation-comms';
import { logger } from '../../../utils';
import { TradeStatus } from '../../../types';
import { tradesGridTemplate } from './tradesgrid.template';
import { EventEmitter } from '@genesislcap/foundation-events';

export type EventMap = StoreEventDetailMap;
@customElement({
  name: 'trades-grid',
  template: tradesGridTemplate,
})
export class TradesGrid extends EventEmitter<EventMap>(FASTElement) {
  @Store store: Store;
  public tradeGrid!: ZeroGridPro;
  public tradesDatasource!: GridProGenesisDatasource;
  tradesGridTemplate: HTMLElement;
  @Connect connect: Connect;

  private sources: Map<string, GridProGenesisDatasource> = new Map<
    string,
    GridProGenesisDatasource
  >();

  connectedCallback() {
    super.connectedCallback();

    this.tradeGrid?.addEventListener('onGridReady', this.handleTradeGridReady);
    this.sources.set('tradesDatasource', this.tradesDatasource);

    this.tradesGridTemplate.addEventListener('filter-changed', this.handleFilterChanged);
    this.tradesGridTemplate.addEventListener('filter-cleared', this.handleFilterCleared);
  }

  disconnectedCallback() {
    super.disconnectedCallback();
    this.tradeGrid?.removeEventListener('onGridReady', this.handleTradeGridReady);
    this.tradeGrid?.gridApi?.removeEventListener(
      Events.EVENT_FIRST_DATA_RENDERED,
      this.handleTradeGridFirstRendered
    );

    this.tradesGridTemplate.removeEventListener('filter-changed', this.handleFilterChanged);
    this.tradesGridTemplate.removeEventListener('filter-cleared', this.handleFilterCleared);
  }

  handleTradeGridFirstRendered = () => {
    this.tradeGrid.gridApi.sizeColumnsToFit();
  };

  handleTradeGridReady = () => {
    this.tradeGrid.gridApi.addEventListener(
      Events.EVENT_FIRST_DATA_RENDERED,
      this.handleTradeGridFirstRendered
    );
  };

  handleFilterChanged = (e) => {
    const fieldName = e?.detail?.fieldName;
    const filter = e?.detail?.filter;
    const target: string = e?.detail?.target;

    fieldName && filter && this.sources.get(target)?.setFilter(fieldName, filter);
  };

  handleFilterCleared = (e) => {
    const fieldName = e?.detail?.fieldName;
    const target: string = e?.detail?.target;

    this.sources.get(target)?.removeFilter(fieldName);
  };

  public singleTradeActionColDef: ColDef = {
    headerName: 'Action',
    minWidth: 110,
    maxWidth: 110,
    cellRenderer: 'action', // AgRendererTypes.action
    cellRendererParams: {
      actionClick: async (rowData) => {
        this.$emit(TRADE_EVENTS.EVENT_SET_ADD_TRADES, rowData);
        const tradeCancelEvent = await this.connect.commitEvent('EVENT_TRADE_CANCEL', {
          DETAILS: {
            TRADE_ID: this.store.addTradeData.TRADE_ID,
          },
        });

        logger.debug('EVENT_TRADE_CANCEL result -> ', tradeCancelEvent);
      },
      actionName: 'Cancel',
      appearance: 'secondary-orange',
      isDisabled: (rowData) => {
        return rowData?.TRADE_STATUS === TradeStatus.CANCELLED;
      },
    },
    pinned: 'right',
  };
}

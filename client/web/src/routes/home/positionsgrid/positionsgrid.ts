import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import {
  ColDef,
  RowSelectedEvent,
  CellClickedEvent,
  PaginationChangedEvent,
} from '@ag-grid-community/core';
import {
  INSTRUMENT_EVENTS,
  MISC_EVENTS,
  Store,
  StoreEventDetailMap,
  TRADE_EVENTS,
} from '../../../store';
import { GridProGenesisDatasource, ZeroGridPro } from '@genesislcap/foundation-zero-grid-pro';
import { debounce, interval, Observable, Subscriber, Subscription } from 'rxjs';
import { positionsGridTemplate } from './positionsgrid.template';
import { EventEmitter } from '@genesislcap/foundation-events';

export type EventMap = StoreEventDetailMap;
@customElement({
  name: 'positions-grid',
  template: positionsGridTemplate,
})
export class PositionsGrid extends EventEmitter<EventMap>(FASTElement) {
  @Store store: Store;
  public positionsGrid!: ZeroGridPro;
  public positionsDatasource!: GridProGenesisDatasource;

  private sources: Map<string, GridProGenesisDatasource> = new Map<
    string,
    GridProGenesisDatasource
  >();

  private positionGridPaginationSubscriber: Subscriber<PaginationChangedEvent>;
  private positionGridPaginationSubscription: Subscription;
  private positionGridPaginationObservable = new Observable((subscriber) => {
    this.positionGridPaginationSubscriber = subscriber;
  });

  connectedCallback(): void {
    super.connectedCallback();

    this.positionGridPaginationSubscription = this.positionGridPaginationObservable
      .pipe(debounce(() => interval(500)))
      .subscribe((e: PaginationChangedEvent) => {
        if (!this.store.selectedInstrumentId) return;
        const renderNodes = e.api.getRenderedNodes();
        renderNodes.some((node) => {
          const isSelectedInstrumentNode =
            node.data.INSTRUMENT_ID === this.store.selectedInstrumentId;
          if (isSelectedInstrumentNode) {
            node.setSelected(true);
          }
          return isSelectedInstrumentNode;
        });
      });

    this.addEventListener('filter-changed', this.handleFilterChanged);
    this.addEventListener('filter-cleared', this.handleFilterCleared);
    this.sources.set('positionsDatasource', this.positionsDatasource);
  }

  public disconnectedCallback(): void {
    this.removeEventListener('filter-changed', this.handleFilterChanged);
    this.removeEventListener('filter-cleared', this.handleFilterCleared);
    this.positionGridPaginationSubscription.unsubscribe();
  }

  handleFilterChanged = (e) => {
    const fieldName = e?.detail?.fieldName;
    const filter = e?.detail?.filter;
    const target: string = e?.detail?.target;

    this.positionsGrid.gridApi.deselectAll();

    fieldName && filter && this.sources.get(target)?.setFilter(fieldName, filter);
  };

  handleFilterCleared = (e) => {
    const fieldName = e?.detail?.fieldName;
    const target: string = e?.detail?.target;

    this.positionsGrid.gridApi.deselectAll();

    this.sources.get(target)?.removeFilter(fieldName);
  };

  @observable positionsGridOptions = {
    suppressRowClickSelection: true,
    onCellClicked: (e: CellClickedEvent) => {
      if (e.column.getColId() !== 'ADD_TRADE' || !e.node.isSelected()) {
        e.node.setSelected(!e.node.isSelected());
      }
    },
    onRowClicked: (e: RowSelectedEvent) => {
      const payload = e.node.isSelected() ? e.data : undefined;
      this.$emit(INSTRUMENT_EVENTS.EVENT_SELECTED_INSTRUMENT_CHANGE, payload);
    },
    onPaginationChanged: (e: PaginationChangedEvent) => {
      this.positionGridPaginationSubscriber.next(e);
    },
  };

  public singlePositionActionColDef: ColDef = {
    headerName: 'Action',
    colId: 'ADD_TRADE',
    minWidth: 130,
    maxWidth: 130,
    cellRenderer: 'action', // AgRendererTypes.action
    cellRendererParams: {
      actionClick: async (rowData) => {
        this.$emit(TRADE_EVENTS.EVENT_SET_ADD_TRADES, {
          INSTRUMENT_ID: rowData.INSTRUMENT_ID,
        });
        this.$emit(MISC_EVENTS.EVENT_SIDE_NAV, 'open');
      },
      actionName: 'Add Trade',
      appearance: 'primary-gradient',
    },
    pinned: 'right',
  };
}

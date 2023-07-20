import { Serialisers, Join } from '@genesislcap/foundation-criteria';
import { CustomEventMap } from '@genesislcap/foundation-events';
import {
  AbstractStoreRoot,
  registerStore,
  StoreRoot,
  StoreRootEventDetailMap,
} from '@genesislcap/foundation-store';
import { observable } from '@microsoft/fast-element';
import { TradeStatus } from '../types';
import { expressionBuilder, criteriaBuilder } from '../utils';

export interface Store extends StoreRoot {
  readonly tradesFilterCriteria: string;
  readonly positionsFilterCriteria: string;
  readonly lineChartFilterCriteria: string;
  readonly pieChartFilterCriteria: string;
  readonly selectedInstrumentId: string;
  readonly sideNavClosed: boolean;
  readonly addTradeData: any;
}

export type SelectedInstrument = {
  readonly INSTRUMENT_ID: string;
  readonly INSTRUMENT_NAME: string;
};

export type SideNavEvent = 'open' | 'close' | 'toggle';

export enum INSTRUMENT_EVENTS {
  EVENT_SELECTED_INSTRUMENT_CHANGE = 'selected-instrument-changed',
  EVENT_SEGMENTED_SELECTED_INSTRUMENT_CHANGE = 'segmented-selected-instrument-changed',
  EVENT_POSITIONS_FILTERS_CHANGE = 'positions-filters-changed',
}

export enum MISC_EVENTS {
  EVENT_SIDE_NAV = 'side-nav-toggle',
}

export enum TRADE_EVENTS {
  EVENT_SET_ADD_TRADES = 'edit-add-trade',

}

export type StoreEventDetailMap = StoreRootEventDetailMap & {
  [INSTRUMENT_EVENTS.EVENT_SELECTED_INSTRUMENT_CHANGE]: SelectedInstrument;
  [MISC_EVENTS.EVENT_SIDE_NAV]: SideNavEvent;
  [TRADE_EVENTS.EVENT_SET_ADD_TRADES]: any;
  [INSTRUMENT_EVENTS.EVENT_POSITIONS_FILTERS_CHANGE]: any;
  [INSTRUMENT_EVENTS.EVENT_SEGMENTED_SELECTED_INSTRUMENT_CHANGE]: any;
};

declare global {
  interface HTMLElementEventMap extends CustomEventMap<StoreEventDetailMap> {}
}

class DefaultStore extends AbstractStoreRoot<Store, StoreEventDetailMap> implements Store {
  @observable tradesFilterCriteria: string;
  @observable positionsFilterCriteria: string;
  @observable lineChartFilterCriteria: string;
  @observable pieChartFilterCriteria: string;
  @observable selectedInstrumentId: string;
  @observable sideNavClosed: boolean = true;
  @observable addTradeData: any = {};

  constructor() {
    super();

    this.createListener<any>(TRADE_EVENTS.EVENT_SET_ADD_TRADES, (payload: any) => {
      this.addTradeData = payload;
    });

    this.createListener<SideNavEvent>(MISC_EVENTS.EVENT_SIDE_NAV, (type: SideNavEvent) => {
      switch (type) {
        case 'open':
          this.sideNavClosed = false;
          break;
        case 'close':
          this.sideNavClosed = true;
          break;
        case 'toggle':
          this.sideNavClosed = !this.sideNavClosed;
          break;
      }
    });

    this.createListener<SelectedInstrument>(
      INSTRUMENT_EVENTS.EVENT_SELECTED_INSTRUMENT_CHANGE,
      (instrument: SelectedInstrument) => {
        if (!instrument) {
          this.tradesFilterCriteria = undefined;
          this.lineChartFilterCriteria = undefined;
          this.pieChartFilterCriteria = undefined;
          this.selectedInstrumentId = undefined;
          return;
        }

        this.tradesFilterCriteria = criteriaBuilder()
          .withExpression(
            expressionBuilder('INSTRUMENT_NAME', instrument.INSTRUMENT_NAME, Serialisers.EQ)
          )
          .build();
        this.pieChartFilterCriteria = criteriaBuilder()
          .withExpression(
            expressionBuilder('INSTRUMENT_ID', instrument.INSTRUMENT_ID, Serialisers.EQ)
          )
          .build();
        this.lineChartFilterCriteria = criteriaBuilder()
          .withExpression([
            expressionBuilder('INSTRUMENT_NAME', instrument.INSTRUMENT_NAME, Serialisers.EQ),
            Join.And(),
            expressionBuilder('TRADE_STATUS', TradeStatus.CANCELLED, Serialisers.NE),
          ])
          .build();
        this.selectedInstrumentId = instrument.INSTRUMENT_ID;
      }
    );

    this.createListener<string>(
      INSTRUMENT_EVENTS.EVENT_SEGMENTED_SELECTED_INSTRUMENT_CHANGE,
      (criteria: string) => {
        if (!criteria) {
          this.tradesFilterCriteria = undefined;
          this.lineChartFilterCriteria = undefined;
          this.pieChartFilterCriteria = undefined;
          this.selectedInstrumentId = undefined;
          return;
        }

        this.tradesFilterCriteria = criteria;
        this.pieChartFilterCriteria = criteria;
        this.lineChartFilterCriteria = criteria;
      }
    );

    this.createListener<string>(
      INSTRUMENT_EVENTS.EVENT_POSITIONS_FILTERS_CHANGE,
      (criteria: string) => {
        if (!criteria) {
          this.tradesFilterCriteria = undefined;
          return;
        }
        this.positionsFilterCriteria = criteria;
      }
    );
  }
}

export const Store = registerStore(DefaultStore, 'RootStore');

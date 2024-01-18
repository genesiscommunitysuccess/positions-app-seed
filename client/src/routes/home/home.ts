import { Connect } from '@genesislcap/foundation-comms';
import { EventEmitter } from '@genesislcap/foundation-events';
import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { HostENV, HostURL } from '../../types';
import { INSTRUMENT_EVENTS, MISC_EVENTS, Store, StoreEventDetailMap, TRADE_EVENTS } from '../../store';
import { logger } from '../../utils';
import { HomeStyles as styles } from './home.styles';
import { HomeTemplate as template } from './home.template';
import { FoundationLayout } from '@genesislcap/foundation-layout';
import { HOME_PREDEFINED_LAYOUT } from './predefined-layouts';

export type EventMap = StoreEventDetailMap;
@customElement({
  name: 'home-route',
  template,
  styles,
})
export class Home extends EventEmitter<EventMap>(FASTElement) {
  @HostENV hostEnv!: string;
  @HostURL hostUrl!: string;
  @Connect connect: Connect;
  @Store store: Store;
  @observable segmentedInstrumentValue;
  segmentedInstrumentValueChanged(oldValue, newValue) {
    this.$emit(INSTRUMENT_EVENTS.EVENT_SEGMENTED_SELECTED_INSTRUMENT_CHANGE, newValue);
  }
  @observable tradesCriteria;
  tradesCriteriaChanged(oldValue, newValue) {
    this.$emit(INSTRUMENT_EVENTS.EVENT_POSITIONS_FILTERS_CHANGE, newValue);
  }

  public addTradeModal: any;

  layout: FoundationLayout;

  public connectedCallback() {
    super.connectedCallback();

    logger.debug(`home-route is now connected to the DOM`);

    this.store.binding('sideNavClosed', (closed) => {
      this.editModalVisibleChanged(closed);
    });

    this.resetLayout = this.resetLayout.bind(this);
  }

  closeNavCallback() {
    this.$emit(MISC_EVENTS.EVENT_SIDE_NAV, 'close');
  }

  resetLayout() {
    this.layout.loadLayout(JSON.parse(HOME_PREDEFINED_LAYOUT));
  }

  editModalVisibleChanged(closed) {
    if (this.addTradeModal) {
      if (closed) {
        this.addTradeModal.close();
      } else {
        this.addTradeModal.show();
      }
    }
  }
}

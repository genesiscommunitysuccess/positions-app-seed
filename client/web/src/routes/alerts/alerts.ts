import { customElement, FASTElement, observable } from '@microsoft/fast-element';
import { AlertsTemplate as template } from './alerts.template';
import { AlertsStyles as styles } from './alerts.styles';
import { Auth, Connect } from '@genesislcap/foundation-comms';
import { EntityManagement } from '@genesislcap/foundation-entity-management';

const name = 'alerts-route';

@customElement({
  name,
  template,
  styles,
})
export class Alerts extends FASTElement {
  @Connect connect: Connect;
  @Auth auth: Auth;

  @observable name: string = '';
  @observable threshold: string = '';
  @observable instrument: string = '';
  @observable message: string = '';
  @observable alerts: EntityManagement;

  public async createAlert() {
    await this.connect.commitEvent(
      'EVENT_DYNAMIC_RULE_NOTIFY_CREATE',
      {
        DETAILS: {
          RULE_NAME: this.name,
          RULE_DESCRIPTION: this.instrument,
          USERNAME: this.auth.currentUser.username,
          MESSAGE: this.message,
          TABLE_ID: 'TRADE_ID',
          RULE_EXPRESSION: `(QUANTITY > ${this.threshold}) && (INSTRUMENT_ID == '${this.instrument}')`,
          RULE_TABLE: 'TRADE',
          TOPIC: 'EMAIL_AND_SCREEN',
        },
      }
    );

    this.alerts.closeModal();
  }
}

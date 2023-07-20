import { ViewTemplate } from '@microsoft/fast-element';
import { css, customElement, FASTElement, html } from '@microsoft/fast-element';
import { Auth, Datasource } from '@genesislcap/foundation-comms';
import {
  ErrorBoundaryEvent,
  getErrorBuilder,
  getNotificationBuilder,
} from '@genesislcap/foundation-errors';

export const NotificationTemplate: ViewTemplate = html`
  <template></template>
`;

@customElement({
  name: 'genesis-notification',
  template: NotificationTemplate,
  styles: css``,
})
export class GenesisNotifications extends FASTElement {
  @Datasource datasource: Datasource;
  @Auth private auth: Auth;

  async connectedCallback() {
    super.connectedCallback();

    const init = await this.datasource.init(
      {
        resourceName: 'ALL_NOTIFY_RECORDS',
      },
      true
    );

    // @ts-ignore
    this.datasource.stream?.subscribe((result: any) => {
      if (!result.ROW) {
        return;
      }

      const details = result?.ROW[0];
      const messageResult = details?.BODY;
      if (result.SEQUENCE_ID > 1) {
        console.log('Message received: ', messageResult);

        const messageObject = JSON.parse(messageResult);

        this.$emit(
          ErrorBoundaryEvent.ERROR_BOUNDARY_EVENT,
          getErrorBuilder()
            .withTitle('Alert')
            .withErrorDetails(messageObject.MESSAGE)
            .withNotification(
              getNotificationBuilder()
                .withType('warning')
                .withAutoClose(true)
                .withCloseTimeout(10000)
                .build()
            )
            .build()
        );
      }
    });
  }
}

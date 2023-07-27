import { css, html, repeat } from '@microsoft/fast-element';
import { FASTElementLayout } from '@microsoft/fast-router';
import { NOTIFICATIONS_EVENTS } from '../../store';
import { Flyout } from '@genesislcap/foundation-ui';
import { ToastButton } from '@genesislcap/foundation-notifications';

const baseLayoutCss = css`
  .container {
    width: 100%;
    height: 100%;
    display: block;
    position: relative;
  }

  .content {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
  }
`;

export const loginLayout = new FASTElementLayout(
  html`
    <div class="container">
      <div class="content">
        <slot></slot>
      </div>
    </div>
  `,
  baseLayoutCss,
);

export const defaultLayout = new FASTElementLayout(
  html`
    <div class="container">
      <foundation-header
        show-luminance-toggle-button
        show-misc-toggle-button
        show-notification-button
        @notification-icon-clicked=${(x) =>
          x.$emit(NOTIFICATIONS_EVENTS.EVENT_CHANGE_INBOX_DISPLAY, true)}
      >
        <div slot="routes" class="routes">
          ${repeat(
            (x) => x.config.allRoutes,
            html`
              <zero-button
                appearance="neutral-grey"
                value="${(x) => x.index}"
                @click=${(x, c) => c.parent.navigation.navigateTo(x.path)}
              >
                <zero-icon variant="${(x) => x.variant}" name="${(x) => x.icon}"></zero-icon>
                ${(x) => x.title}
              </zero-button>
            `,
          )}
        </div>
        <div slot="menu-contents">
          <zero-button
            appearance="neutral-grey"
            @click=${(x, c) => {
              const { resetLayout } = x.lastChild;
              resetLayout();
            }}
          >
            Reset Layout
          </zero-button>
        </div>
        <foundation-inbox-counter slot="notifications-icon-end"></foundation-inbox-counter>
      </foundation-header>
      <zero-flyout
        position="right"
        @closed=${(x) => x.$emit(NOTIFICATIONS_EVENTS.EVENT_CHANGE_INBOX_DISPLAY, false)}
        closed=${(x) => !x.store.inboxDisplayState}
        displayHeader=${false}
      >
        <foundation-inbox
          @close=${(ignore, c) => (<Flyout>(<HTMLElement>c.event.target).parentNode).closeFlyout()}
        ></foundation-inbox>
      </zero-flyout>
      <zero-notification-listener resource-name="ALL_NOTIFY_ALERT_RECORDS">
        <div class="content">
          <slot></slot>
        </div>
      </zero-notification-listener>
    </div>
  `,
  css`
    :host {
      --nav-height: 60px;
    }

    ${baseLayoutCss}

    foundation-header {
      position: absolute;
      top: 0;
      left: 0;
      width: calc(100% - 19px);
      height: var(--nav-height);
      align-items: center;
      padding-left: calc(var(--design-unit) * 3px);
      padding-right: calc(var(--design-unit) * 3px);
      border: none;
    }

    .routes {
      margin-bottom: calc(var(--design-unit) * 1px);
    }

    .content {
      top: var(--nav-height);
    }

    foundation-header::part(notifications-button) {
      position: relative;
    }

    foundation-inbox-counter {
      z-index: 999;
      position: absolute;
      top: 29px;
      right: 0;
      pointer-events: none;
    }

    zero-flyout::part(flyout) {
      width: 40%;
      min-width: 320px;
      padding: 0;
    }

    zero-flyout::part(content) {
      height: 100%;
    }
  `,
);

import { css, html, repeat } from '@microsoft/fast-element';
import { FASTElementLayout } from '@microsoft/fast-router';

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
  baseLayoutCss
);

export const defaultLayout = new FASTElementLayout(
  html`
    <div class="container">
      <foundation-header
        show-luminance-toggle-button
        show-misc-toggle-button
        show-notification-button
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
            `
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
      </foundation-header>
      <div class="content">
        <slot></slot>
      </div>
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
  `
);

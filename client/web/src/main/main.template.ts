import { ExecutionContext, html, ref } from '@microsoft/fast-element';
import { customEvent } from '@genesislcap/foundation-events';
import type { MainApplication } from './main';

function eventDetail<T = any>(ctx: ExecutionContext) {
  return (ctx.event as CustomEvent).detail as T;
}

export const DynamicTemplate = html<MainApplication>`
  <template
    ${ref('rootProvider')}
    @store-connected=${(x, c) => x.store.onConnected(customEvent(c))}
  >
    <zero-design-system-provider with-defaults ${ref('provider')}>
      <div
        id="dynamic-template"
        @doing-something=${(x, c) => x.handleDoingSomething(eventDetail(c))}
      >
        ${(x) => x.selectTemplate()}
      </div>
    </zero-design-system-provider>
  </template>
`;

export const LoadingTemplate = html<MainApplication>`
  ...
`;

export const MainTemplate = html<MainApplication>`
  <fast-router :config=${(x) => x.config} :navigation=${(x) => x.navigation}></fast-router>
`;

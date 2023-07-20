import { html } from '@microsoft/fast-element';
import type { Extras } from './extras';

export const ExtrasTemplate = html<Extras>`
  <div class="split-layout">
    <div class="top-layout">
      <zero-card class="positions-card">
        <span class="card-title">Positions (infinite scroll)</span>
        <zero-grid-pro>
          <grid-pro-genesis-datasource-next resource-name="ALL_TRADES" />
        </zero-grid-pro>
      </zero-card>
    </div>

    <zero-divider appearance="accent"></zero-divider>

    <div class="top-layout">
      <zero-card>
        <zero-grid-pro>
          <grid-pro-genesis-datasource-next resource-name="ALL_TRADES" pagination="true" />
        </zero-grid-pro>
      </zero-card>
    </div>
  </div>
`;

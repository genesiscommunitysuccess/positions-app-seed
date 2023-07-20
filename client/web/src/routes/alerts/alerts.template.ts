import { sync } from '@genesislcap/foundation-utils';
import { html, ref } from '@microsoft/fast-element';
import type { Alerts } from './alerts';

export const positionColumnDefs: any[] = [
  { field: 'RULE_NAME', headerName: 'Name', sort: 'desc' },
  { field: 'RULE_DESCRIPTION', headerName: 'Symbol' },
  { field: 'RULE_EXPRESSION', headerName: 'Rule' },
];

export const AlertsTemplate = html<Alerts>`
  <entity-management
    title="Trade alerts rule"
    :columns=${() => positionColumnDefs}
    resourceName="ALL_DYNAMIC_NOTIFY_RULES"
    deleteEvent="EVENT_DYNAMIC_RULE_NOTIFY_DELETE"
    createEvent="EVENT_DYNAMIC_RULE_NOTIFY_CREATE"
    ${ref('alerts')}
  >
    <div slot="edit">
      <zero-flex-layout class="flex-column spacing-2x">
        <zero-text-field :value=${sync((x) => x.name)}>Rule Name</zero-text-field>
        <span>Instrument</span>
        <zero-combobox :value=${sync((x) => x.instrument)}>
          <options-datasource
            resourceName="ALL_INSTRUMENTS"
            value-field="INSTRUMENT_ID"
          ></options-datasource>
        </zero-combobox>
        <zero-text-field :value=${sync((x) => x.threshold)}>Threshold</zero-text-field>
        <zero-text-field :value=${sync((x) => x.message)}>Message</zero-text-field>
        <zero-button @click=${(x) => x.createAlert()}>Submit</zero-button>
      </zero-flex-layout>
    </div>
  </entity-management>
`;

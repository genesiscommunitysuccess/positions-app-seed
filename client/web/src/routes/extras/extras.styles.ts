import { css } from '@microsoft/fast-element';
import { mixinScreen } from '../../styles';

export const ExtrasStyles = css`
  :host {
    ${mixinScreen('flex')}

    align-items: center;
    justify-content: center;
    flex-direction: column;
  }

  .split-layout {
    display: flex;
    flex-direction: column;
    flex: 1;
    width: 100%;
  }

  .split-layout zero-divider {
    margin: 0 calc(var(--design-unit) * 3px);
  }

  .top-layout zero-card {
    margin: calc(var(--design-unit) * 3px);
  }

  zero-ag-grid {
    width: 100%;
    height: 100%;
  }

  .top-layout {
    height: 50%;
    display: flex;
    flex-direction: row;
  }

  zero-card {
    display: flex;
    height: var(--card-height);
    flex-direction: column;
    flex: 1;
    margin: calc(var(--design-unit) * 3px);
  }

  .card-title {
    padding: calc(var(--design-unit) * 3px);
    background-color: #22272a;
    font-size: 13px;
    font-weight: bold;
  }
`;

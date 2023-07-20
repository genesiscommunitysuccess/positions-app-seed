import { css } from '@microsoft/fast-element';
import { mixinScreen } from '../../styles';

export const HomeStyles = css`
  :host {
    ${mixinScreen('flex')}

    align-items: center;
    justify-content: center;
    flex-direction: row;

    --zero-card-fill-color: rgb(34, 39, 42);
    --neutral-stroke-divider-rest: var(--neutral-fill-stealth-rest);
    --action-height-multiplier: 1px;
  }

  .column-split-layout {
    display: flex;
    flex-direction: column;
    flex: 1;
    width: 100%;
  }

  .row-split-layout {
    display: flex;
    flex-direction: row;
    flex: 1;
    width: 100%;
    height: 50%;
  }

  .top-layout {
    width: 50%;
    display: flex;
    flex-direction: row;
  }

  .top-layout zero-card {
    display: flex;
    height: var(--card-height);
    flex-direction: column;
    width: inherit;
    flex: 1;
    margin: calc(var(--design-unit) * 3px);
  }

  .chart-card {
    padding: calc(var(--design-unit) * 3px);
  }

  zero-grid-pro {
    width: 100%;
    height: 100%;
  }

  zero-charts {
    width: 100%;
    height: 100%;
  }

  .card-title {
    padding: calc(var(--design-unit) * 3px);
    background-color: #22272a;
    font-size: 13px;
    font-weight: bold;
  }

  .close-icon {
    position: absolute;
    right: calc(var(--design-unit) * 2px);
    top: calc(var(--design-unit) * 2px);
  }

  .close-icon zero-icon {
    cursor: pointer;
  }

  .counter-container {
    display: flex;
    flex-direction: column;
  }

  .form-title,
  .form-counter {
    font-family: Roboto-Medium, sans-serif;
    font-style: normal;
    color: var(--neutral-stroke-rest);
  }

  .form-counter {
    font-size: var(--type-ramp-base-font-size);
    line-height: var(--type-ramp-base-line-height);
    margin-bottom: 2px;
    margin-top: calc(var(--design-unit) * 5px);
  }

  zero-button {
    width: 50%;
    align-self: flex-end;
    margin-top: calc(var(--design-unit) * 6px);
  }

  criteria-segmented-control {
    display: flex;
    justify-content: center;
  }

  .filter-card {
    padding-top: 10px;
    width: 22%;
    margin-right: 10px;
  }

  .filters-label {
    width: 100%;
    color: rgb(135, 155, 166);
    text-align: center;
  }
`;

import { DI } from '@microsoft/fast-foundation';

export const HostENV = DI.createInterface<string>();

export const HostURL = DI.createInterface<string>();

export enum SIDE {
  BUY = 'BUY',
  SELL = 'SELL',
}

export enum TradeStatus {
    NEW = 'NEW',
    CANCELLED = 'CANCELLED'
}

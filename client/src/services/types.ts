export interface Percentage {
  key: string;
  value: number;
  appearance: string;
}

export enum RFT {
  SPD = 'SPD',
  BID = 'BID',
}

export interface SPD {
  key: RFT & RFT.SPD;
  value: string;
  appearance: string;
}

export interface BID {
  key: RFT & RFT.BID;
  value: string;
  appearance: string;
}

export interface DealerInfo {
  count: number;
}

export interface TTL {
  totalSecs: number;
  remainSecs: number;
  remainPercent: number;
  classNames?: string;
}

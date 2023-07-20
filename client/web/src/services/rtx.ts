import { Auth, Connect, SocketObservable } from '@genesislcap/foundation-comms';
import { DI } from '@microsoft/fast-foundation';
import { of, range, timer } from 'rxjs';
import { concatMap, delay, map, take } from 'rxjs/operators';
import { BID, DealerInfo, Percentage, SPD, TTL } from './types';
import { logger } from '../utils';

/**
 * I have no idea what this service and the RTX UI needs to actually do!
 */
export interface RTX {
  sendRFT(type: SPD | BID): Promise<void>;
  sendPercentage(percentage: Percentage): Promise<void>;
  getDealerInfo$(
    onMessage: Function,
    onError: Function,
    params?: any
  ): SocketObservable<DealerInfo>;
  getTTL$(onMessage: Function, onError: Function, params?: any): SocketObservable<TTL>;
}

class RTXService implements RTX {
  constructor(@Connect private connect: Connect, @Auth private auth: Auth) {}

  public async sendRFT(type: SPD | BID): Promise<void> {
    logger.debug(`sendRFT ${JSON.stringify(type)}`);
  }

  public async sendPercentage(percentage: Percentage): Promise<void> {
    logger.debug(`sendPercentage ${percentage.value}`);
  }

  public getDealerInfo$(
    onMessage: Function,
    onError: Function,
    params?: any
  ): SocketObservable<DealerInfo> {
    const maxDelay = 5_000;
    return range(1, 100).pipe(
      map((count): DealerInfo => ({ count })),
      concatMap((dealerInfo) => of(dealerInfo).pipe(delay(Math.random() * maxDelay)))
    );
  }

  public getTTL$(onMessage: Function, onError: Function, params?: any): SocketObservable<TTL> {
    const totalSecs = 60;
    const intervalDuration = 1_000;
    return timer(0, intervalDuration).pipe(
      take(totalSecs + 1),
      map((tickSecs) => {
        const remainSecs = totalSecs - tickSecs;
        const remainPercent = Math.floor((remainSecs / totalSecs) * 100);
        return {
          totalSecs,
          remainSecs,
          remainPercent,
        };
      })
    );
  }
}

export const RTX = DI.createInterface<RTX>((x) => x.singleton(RTXService));

import { ColDef } from '@ag-grid-community/core';
import { formatDateLong, formatNumber } from '../../utils/formatting';
import { SIDE, TradeStatus } from '../../types';

const tradeCellClassRules = {
  'buy-side-trade': (params) => params.value === SIDE.BUY,
  'sell-side-trade': (params) => params.value === SIDE.SELL,
  'new-status-trade': (params) => params.value === TradeStatus.NEW,
  'cancel-status-trade': (params) => params.value === TradeStatus.CANCELLED,
};
export const tradeColumnDefs: ColDef[] = [
  {
    field: 'SIDE',
    headerName: 'Side',
    cellClass: 'status-cell',
    cellClassRules: tradeCellClassRules,
    enableCellChangeFlash: true,
    minWidth: 80,
    flex: 1,
  },
  {
    field: 'QUANTITY',
    headerName: 'Quantity',
    valueFormatter: formatNumber(0),
    type: 'rightAligned',
    enableCellChangeFlash: true,
    flex: 1,
  },
  {
    field: 'PRICE',
    headerName: 'Price',
    valueFormatter: formatNumber(2),
    type: 'rightAligned',
    enableCellChangeFlash: true,
    flex: 2,
  },
  {
    field: 'CONSIDERATION',
    headerName: 'Consideration',
    valueFormatter: formatNumber(2),
    type: 'rightAligned',
    enableCellChangeFlash: true,
    flex: 2,
  },
  {
    field: 'TRADE_DATETIME',
    headerName: 'Date',
    valueFormatter: (rowData) => formatDateLong(rowData.data.TRADE_DATETIME),
    sort: 'desc',
    enableCellChangeFlash: true,
    flex: 2,
  },
  {
    field: 'COUNTERPARTY_NAME',
    headerName: 'Counterparty',
    enableCellChangeFlash: true,
    flex: 2,
  },
  {
    field: 'TRADE_STATUS',
    headerName: 'Trade State',
    cellClass: 'status-cell',
    cellClassRules: tradeCellClassRules,
    enableCellChangeFlash: true,
    flex: 2,
  },
  {
    field: 'ENTERED_BY',
    headerName: 'Entered By',
    enableCellChangeFlash: true,
    flex: 2,
  },
];

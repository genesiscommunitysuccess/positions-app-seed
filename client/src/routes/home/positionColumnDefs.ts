import { ColDef } from '@ag-grid-community/core';
import { formatNumber } from '../../utils/formatting';

export const positionColumnDefs: ColDef[] = [
  { field: 'INSTRUMENT_NAME', headerName: 'Instrument', sort: 'desc', flex: 2 },
  {
    field: 'QUANTITY',
    headerName: 'Quantity',
    valueFormatter: formatNumber(0),
    type: 'rightAligned',
    flex: 1,
    enableCellChangeFlash: true,
  },
  {
    field: 'CURRENCY',
    headerName: 'Ccy',
    flex: 1,
    enableCellChangeFlash: true,
  },
  {
    field: 'NOTIONAL',
    headerName: 'Traded Value',
    valueFormatter: formatNumber(2),
    type: 'rightAligned',
    flex: 1,
    enableCellChangeFlash: true,
  },
  {
    field: 'VALUE',
    headerName: 'Market Value',
    valueFormatter: formatNumber(2),
    type: 'rightAligned',
    flex: 1,
    enableCellChangeFlash: true,
  },
  {
    field: 'PNL',
    headerName: 'PNL',
    valueFormatter: formatNumber(2),
    type: 'rightAligned',
    flex: 1,
    enableCellChangeFlash: true,
  },
];

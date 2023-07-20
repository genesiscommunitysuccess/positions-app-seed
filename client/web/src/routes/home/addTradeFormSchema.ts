export const addTradeFormSchema = {
  type: 'VerticalLayout',
  elements: [
    {
      type: 'Control',
      scope: '#/properties/QUANTITY',
      label: 'Quantity',
    },
    {
      type: 'Control',
      scope: '#/properties/PRICE',
      label: 'Price',
    },
    {
      type: 'Control',
      scope: '#/properties/COUNTERPARTY_ID',
      options: {
        allOptionsResourceName: 'COUNTERPARTY',
        valueField: 'COUNTERPARTY_ID',
        labelField: 'NAME',
      },
      label: 'Counterparty',
    },
    {
      type: 'Control',
      scope: '#/properties/INSTRUMENT_ID',
      options: {
        allOptionsResourceName: 'INSTRUMENT',
        valueField: 'INSTRUMENT_ID',
        labelField: 'NAME',
      },
      label: 'Instrument',
    },
    {
      type: 'Control',
      scope: '#/properties/SIDE',
      label: 'Side',
    },
  ],
};

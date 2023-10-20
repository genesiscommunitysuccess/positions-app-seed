export const allPositionsFilterSchema = {
  type: 'VerticalLayout',
  elements: [
    {
      type: 'Control',
      label: 'Quantity',
      scope: '#/properties/QUANTITY',
    },
    {
      type: 'Control',
      label: 'Notional',
      scope: '#/properties/NOTIONAL',
    },
    {
      type: 'Control',
      label: 'Value',
      scope: '#/properties/VALUE',
    },
    {
        type: 'Control',
        label: 'Currency',
        scope: '#/properties/CURRENCY',
      },
  ],
};

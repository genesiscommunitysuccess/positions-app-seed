export const columnsArray = (config) =>
  Object.keys(config).map((field) => ({
    field,
    ...config[field],
    filter: true,
    sortable: true,
    resizable: true,
  }));

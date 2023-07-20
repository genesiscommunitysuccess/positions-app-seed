/**
 * Number formatter, can be used directly on ag-grid columns as valueFormatter.
 *
 * @param minDP min decimal points
 * @param maxDP max decimal points
 */
export function formatNumber(minDP = 2, maxDP = minDP) {
  return (params) => {
    if (!(params && typeof params.value === 'number')) return '';
    const lang = (navigator && navigator.language) || 'en-GB';
    return Intl.NumberFormat(lang, {
      minimumFractionDigits: minDP,
      maximumFractionDigits: maxDP,
    }).format(params.value);
  };
}

export const formatDateLong = (param: number): string => {
  if (!(param && typeof param === 'number' && param > 0)) return '';
  const date = new Date(param);

  const formattedDate = new Intl.DateTimeFormat(Intl.DateTimeFormat().resolvedOptions().locale, {
    year: 'numeric',
    month: 'short',
    day: '2-digit',
  }).format(date);
  return formattedDate;
};

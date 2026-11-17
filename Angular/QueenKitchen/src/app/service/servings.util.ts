/** Multiply simple numeric recipe amounts without guessing nonnumeric units. */
export function scaledAmount(raw: string, original: number, desired: number): string {
  if (!raw || original <= 0 || desired <= 0) return raw;
  const match = raw.trim().match(/^(\d+(?:\.\d+)?)(\s*.*)$/);
  if (!match) return raw;
  const scaled = Number(match[1]) * desired / original;
  return String(Math.round(scaled * 100) / 100) + match[2];
}

import { scaledAmount } from './servings.util';
describe('scaledAmount', () => {
  it('scales numeric quantities', () => expect(scaledAmount('2 cups', 2, 4)).toBe('4 cups'));
  it('preserves nonnumeric measures', () => expect(scaledAmount('a pinch', 2, 4)).toBe('a pinch'));
  it('does not divide by zero', () => expect(scaledAmount('2 cups', 0, 4)).toBe('2 cups'));
});

const { test, expect } = require('@playwright/test');

test('register, publish, search, favorite and revisit after reload', async ({ page }) => {
  const account = 'chef' + Math.random().toString(36).slice(2, 10);
  const title = 'CI Tomato Soup ' + account;
  await page.goto('/');
  await page.getByRole('button', { name: 'Register' }).click();
  await page.getByLabel('Username').fill(account);
  await page.getByLabel('Email').fill(account + '@example.test');
  await page.getByLabel('Password').fill('testPassword123');
  await page.getByRole('button', { name: 'Create account' }).click();
  await expect(page.getByText('Account created. Please sign in.')).toBeVisible();
  await page.getByLabel('Password').fill('testPassword123');
  await page.getByRole('button', { name: 'Sign in', exact: true }).last().click();
  await expect(page.getByRole('heading', { name: 'Your recipe collection' })).toBeVisible();

  await page.getByRole('button', { name: /Create recipe/ }).click();
  await page.locator('input#title').fill(title);
  await page.getByLabel('Description').fill('Fresh tomato soup');
  await page.getByPlaceholder('Ingredient', { exact: true }).fill('Tomato');
  await page.getByPlaceholder('Amount and unit (e.g. 2 cups)').fill('2 cups');
  await page.getByRole('button', { name: 'Add', exact: true }).first().click();
  await page.getByPlaceholder('Step title').fill('Simmer');
  await page.getByPlaceholder('Instructions').fill('Heat the tomatoes');
  await page.getByRole('button', { name: 'Add', exact: true }).last().click();
  await page.getByRole('button', { name: 'Save recipe' }).click();
  await expect(page.getByText('Recipe saved.')).toBeVisible();
  await page.getByPlaceholder('Search by title').fill(account);
  await page.getByRole('button', { name: 'Search' }).click();
  const card = page.locator('article.card', { hasText: title });
  await expect(card).toBeVisible();

  await card.getByRole('button', { name: /Favorite/ }).click();
  await page.getByRole('button', { name: /Favorites \(/ }).click();
  await expect(page.locator('article.card', { hasText: title })).toBeVisible();

  await page.reload();
  await page.getByRole('button', { name: /Favorites \(/ }).click();
  await expect(page.locator('article.card', { hasText: title })).toBeVisible();
  await page.locator('article.card', { hasText: title }).getByRole('button', { name: '+ Shopping list' }).click();
  await page.getByRole('button', { name: 'Shopping list', exact: true }).click();
  await expect(page.getByText('Tomato — 2 cups')).toBeVisible();
  await page.reload();
  await page.getByRole('button', { name: 'Shopping list', exact: true }).click();
  await expect(page.getByText('Tomato — 2 cups')).toBeVisible();
});

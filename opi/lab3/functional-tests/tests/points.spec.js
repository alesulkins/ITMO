const {test, expect} = require('@playwright/test');
const API_URL = process.env.API_URL || 'http://localhost:8080/api';

async function registerAndGetToken(username, password) {
    const response = await fetch(`${API_URL}/auth/register`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({username, password}), //юзер
    });
    const data = await response.json(); //ответ от бэка 
    return data.token;
}

async function loginWithToken(page, token) {
    await page.goto('/', {waitUntil: 'domcontentloaded'});
    await page.evaluate((t) => localStorage.setItem('authToken', t), token); //эвалюэйл - на открытой странице - скрипт положить токен локстор
    await page.goto('/app', {waitUntil: 'domcontentloaded'});
}

test.beforeEach(async ({page}) => {
    await page.goto('/', {waitUntil: 'domcontentloaded'});
    await page.evaluate(() => localStorage.clear());
});

test('009: незалогиненный пользователь перенаправляется с /app на /', {tag: ['@points', '@negative', '@access']}, async ({page}) => {
    await page.goto('/app', {waitUntil: 'domcontentloaded'});
    await expect(page).toHaveURL(/\/$/);
});

test('010: valid point adds string to table', {tag: ['@points', '@smoke', '@positive']}, async ({page}) => {
    const token = await registerAndGetToken(`pointuser_${Date.now()}`, 'password');
    await loginWithToken(page, token); // подготовка предусловия

    await page.locator('input[name="x"][value="2"]').check();
    await page.locator('#y-input').fill('1');
    await page.locator('input[name="r"][value="3"]').check();
    await page.getByRole('button', {name: 'Проверить точку'}).click();

    const row = page.locator('table tbody tr').first();
    await expect(row).toBeVisible();

    await expect(row.locator('td').nth(0)).toHaveText('2');
    await expect(row.locator('td').nth(1)).toHaveText('1');
    await expect(row.locator('td').nth(2)).toHaveText('3');
});

test('011: invalid y shows error message', {tag: ['@points', '@negative', '@validation']}, async ({page}) => {
    const token = await registerAndGetToken(`yrange_${Date.now()}`, 'password');
    await loginWithToken(page, token);

    await page.locator('input[name="x"][value="1"]').check();
    await page.locator('#y-input').fill('10');
    await page.locator('input[name="r"][value="2"]').check();
    await page.getByRole('button', {name: 'Проверить точку'}).click();

    await expect(page.getByText('Y должен быть числом в диапазоне (-3; 5)')).toBeVisible();
});

test('012: empty y shows error message', {tag: ['@points', '@negative', '@validation']}, async ({page}) => {
    const token = await registerAndGetToken(`yempty_${Date.now()}`, 'password');
    await loginWithToken(page, token);

    await page.locator('input[name="x"][value="1"]').check();
    await page.locator('input[name="r"][value="2"]').check();
    await page.getByRole('button', {name: 'Проверить точку'}).click();

    await expect(page.getByText('Y должен быть числом в диапазоне (-3; 5)')).toBeVisible();
});

test('013: point inside area shows correct result in table', {tag: ['@points', '@positive']}, async ({page}) => {
    const token = await registerAndGetToken(`hit_${Date.now()}`, 'password');
    await loginWithToken(page, token);

    await page.locator('input[name="x"][value="1"]').check();
    await page.locator('#y-input').fill('-1');
    await page.locator('input[name="r"][value="2"]').check();
    await page.getByRole('button', {name: 'Проверить точку'}).click();

    const hitCell = page.locator('table tbody tr').first().locator('td').nth(3);
    await expect(hitCell).toHaveText('Да');
});

test('014: point outside area shows correct result in table', {tag: ['@points', '@positive']}, async ({page}) => {
    const token = await registerAndGetToken(`miss_${Date.now()}`, 'password');
    await loginWithToken(page, token);
    
    await page.locator('input[name="x"][value="1"]').check();
    await page.locator('#y-input').fill('1');
    await page.locator('input[name="r"][value="2"]').check();
    await page.getByRole('button', {name: 'Проверить точку'}).click();

    const hitCell = page.locator('table tbody tr').first().locator('td').nth(3);
    await expect(hitCell).toHaveText('Нет');
});

test('015: logout button works correctly', {tag: ['@points', '@positive']}, async ({page}) => {
    const token = await registerAndGetToken(`logout_${Date.now()}`, 'password');
    await loginWithToken(page, token);

    await expect(page).toHaveURL(/\/app$/);
    await page.getByRole('button', {name: 'Выйти'}).click();
    await expect(page).toHaveURL(/\/$/);

    const storedToken = await page.evaluate(() => localStorage.getItem('authToken'));
    expect(storedToken).toBeNull();
});
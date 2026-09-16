/*


запустить всё / посмотреть список
    ./gradlew func-tests
    ./gradlew func-tests-local
    npx playwright test
    npx playwright test --list
1. по файлу
    npx playwright test tests/auth.spec.js
2. по n строки в которой регистрируют
    npx playwright test tests/auth.spec.js:110
3. по названию: -g / --grep
    npx playwright test -g "006"
    npx playwright test -g "registration"
4. исключить: --grep-invert
    npx playwright test --grep-invert @negative
5. по тегам
    npx playwright test --grep @auth
    npx playwright test --grep "@auth|@points"
6. упавшие в ласт раз
    npx playwright test --last-failed
7. повторы (каждый тест по 3 раза)
    npx playwright test --repeat-each 3
8. параллельное выполнение одного воркера
    npx playwright test --workers 1

*/

const { test, expect } = require('@playwright/test');
const API_URL = process.env.API_URL || 'http://localhost:8080/api';

async function registerApi(username, password) {
    return fetch(`${API_URL}/auth/register`, {
        method: 'POST', // не гет тк создаем юзера
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
    });
}
// чистим локстор
test.beforeEach(async ({ page }) => {
    await page.goto('/', {waitUntil: 'domcontentloaded'});
    await page.evaluate(() => localStorage.clear());
});

test('001: successful registration of a new user', {tag: ['@auth', '@smoke', '@positive', '@fast']}, async ({ page }) => { // помечаем тест тегом @fast, будет запускаться при фильтре --grep @fast
    const username = `user${Date.now()}`;

    await page.getByRole('button', { name: 'Создать новый аккаунт' }).click();
    await page.getByPlaceholder('Логин').fill(username);
    await page.getByPlaceholder('Пароль').fill('password');
    await page.getByRole('button', { name: 'Создать аккаунт' }).click();

    await expect(page).toHaveURL(/\/app$/);
});

test('002: failed registration with existing username', {tag: ['@auth', '@negative']}, async ({ page }) => {
    const username = `duplicate_${Date.now()}`;
    await registerApi(username, 'password');

    await page.getByRole('button', { name: 'Создать новый аккаунт' }).click();
    await page.getByPlaceholder('Логин').fill(username);
    await page.getByPlaceholder('Пароль').fill('password2');
    await page.getByRole('button', { name: 'Создать аккаунт' }).click();

    await expect(page.getByText('уже существует')).toBeVisible();
});

test('003: failed registration with empty username', {tag: ['@auth', '@negative', '@validation']}, async ({ page }) => {
    await page.getByRole('button', { name: 'Создать новый аккаунт' }).click();
    await page.getByPlaceholder('Пароль').fill('password');
    await page.getByRole('button', { name: 'Создать аккаунт' }).click();

    await expect(page.getByText('Введите логин и пароль')).toBeVisible();
});

test('004: failed registration with empty password', {tag: ['@auth', '@negative', '@validation']}, async ({ page }) => {
    await page.getByRole('button', { name: 'Создать новый аккаунт' }).click();
    await page.getByPlaceholder('Логин').fill('user');
    await page.getByRole('button', { name: 'Создать аккаунт' }).click();

    await expect(page.getByText('Введите логин и пароль')).toBeVisible();
});

test('005: switching between login and registration forms', {tag: ['@auth', '@positive']}, async ({ page }) => {
    await expect(page.getByRole('heading', { name: 'Вход в систему' })).toBeVisible();

    await page.getByRole('button', { name: 'Создать новый аккаунт' }).click();
    await expect(page.getByRole('heading', { name: 'Регистрация' })).toBeVisible();

    await page.getByRole('button', { name: 'У меня уже есть аккаунт' }).click();
    await expect(page.getByRole('heading', { name: 'Вход в систему' })).toBeVisible();
});

test('006: successful login redirects to /app', {tag: ['@auth', '@smoke', '@positive']}, async ({ page }) => {
    const username = `loginuser_${Date.now()}`;
    await registerApi(username, 'password');

    await page.getByPlaceholder('Логин').fill(username);
    await page.getByPlaceholder('Пароль').fill('password');
    await page.getByRole('button', { name: 'Войти' }).click();

    await expect(page).toHaveURL(/\/app$/);
});

test('007: failed login with incorrect password shows error', {tag: ['@auth', '@negative']}, async ({ page }) => {
    const username = `wrongpass_${Date.now()}`;
    await registerApi(username, 'correctpassword');

    await page.getByPlaceholder('Логин').fill(username);
    await page.getByPlaceholder('Пароль').fill('wrongpassword');
    await page.getByRole('button', { name: 'Войти' }).click();

    await expect(page.getByText('Неверный логин или пароль')).toBeVisible();
});

test('008: failed login with non-existent username shows error', {tag: ['@auth', '@negative']}, async ({ page }) => {
    await page.getByPlaceholder('Логин').fill('nonexistentuser');
    await page.getByPlaceholder('Пароль').fill('password');
    await page.getByRole('button', { name: 'Войти' }).click();

    await expect(page.getByText('Неверный логин или пароль')).toBeVisible();
});

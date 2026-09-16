const { defineConfig } = require('@playwright/test');

module.exports = defineConfig({
    testDir: './tests',
    retries: 1,
    timeout: 60000,

    // лист построчный вывод каждого теста в консоль, хтмл отчёт в playwright-report/
    reporter: [['list'], ['html', {open: 'never'}]],

    use: {
        headless: true,
        navigationTimeout: 20000,
        actionTimeout: 15000,
        baseURL: process.env.BASE_URL || 'http://localhost:3000',
        screenshot: 'only-on-failure',
    },

    projects: [
        {
            name: 'chromium',
            use: { browserName: 'chromium' },
        },
    ],
});

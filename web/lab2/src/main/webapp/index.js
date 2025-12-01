"use strict";

const state = {
    x: null,
    y: null,
    r: null,
};

const table = document.getElementById("result-table");
const error = document.getElementById("error");
const possibleXs = new Set([-3, -2, -1, 0, 1, 2, 3, 4, 5]);
const decimalRegex = /^[+-]?(?:\d+(?:[.,]\d+)?|[.,]\d+)$/;

const normalizeDecimalInput = (value) => {
    if (value === null || value === undefined) return '';
    return String(value).trim().replace(/,/g, '.');
};

const validateState = (state) => {
    if (!Array.isArray(state.x) || state.x.length === 0) {
        error.hidden = false;
        error.innerText = "Выберите ровно одно значение X";
        throw new Error("Невалидное значение");
    }

    if (state.x.length > 1) {
        error.hidden = false;
        error.innerText = "Можно выбрать только одно значение X";
    }

    if (!possibleXs.has(parseInt(state.x[0]))) {
        error.hidden = false;
        error.innerText = `X должен быть в диапазоне [${[...possibleXs].join(", ")}]`;
        throw new Error("Невалидное значение");
    }

    const yRaw = String(state.y ?? '').trim();
    if (!decimalRegex.test(yRaw)) {
        error.hidden = false;
        error.innerText = "Y должен быть числом";
        throw new Error("Невалидное значение");
    }
    const yNum = parseFloat(normalizeDecimalInput(yRaw));
    if (!(yNum > -5 && yNum < 3)) {
        error.hidden = false;
        error.innerText = "Y должен быть в строго диапазоне (-5, 3)";
        throw new Error("Невалидное значение");
    }

    const rRaw = String(state.r ?? '').trim();
    if (!decimalRegex.test(rRaw)) {
        error.hidden = false;
        error.innerText = "R должен быть числом";
        throw new Error("Невалидное значение");
    }
    const rNum = parseFloat(normalizeDecimalInput(rRaw));
    if (!(rNum > 2 && rNum < 5)) {
        error.hidden = false;
        error.innerText = "R должен быть в строго диапазоне (2, 5)";
        throw new Error("Невалидное значение");
    }
}

const updateXState = () => {
    const xSelect = document.getElementById('x-select');
    if (xSelect && xSelect.value) {
        state.x = [xSelect.value];
    } else {
        state.x = [];
    }
};

document.getElementById('x-select').addEventListener('change', (ev) => {
    updateXState();
});

document.getElementById("y").addEventListener("input", (ev) => {
    state.y = ev.target.value;
});

document.getElementById("y").addEventListener("change", (ev) => {
    state.y = ev.target.value;
});

document.getElementById("r").addEventListener("input", (ev) => {
    state.r = ev.target.value;
    try { localStorage.setItem('savedR', state.r ?? ''); } catch (e) {}
    redrawGraph();
});

document.getElementById("r").addEventListener("change", (ev) => {
    state.r = ev.target.value;
    try { localStorage.setItem('savedR', state.r ?? ''); } catch (e) {}
    redrawGraph();
});

document.getElementById("data-form").addEventListener("submit", function (ev) {
    const yEl = document.getElementById('y');
    state.y = yEl ? String(yEl.value).trim() : '';
    const rEl = document.getElementById('r');
    state.r = rEl ? String(rEl.value).trim() : '';
    updateXState();

    const hasX = Array.isArray(state.x) && state.x.length === 1;
    const hasY = state.y !== null && String(state.y).trim().length > 0;
    const hasR = state.r !== null && String(state.r).trim().length > 0;
    if (!hasX || !hasY || !hasR) {
        ev.preventDefault();
        error.hidden = false;
        if (!hasX && !hasY && !hasR) {
            error.innerText = 'Заполните X, Y и R';
        } else if (!hasX && !hasY) {
            error.innerText = 'Заполните X и Y';
        } else if (!hasY && !hasR) {
            error.innerText = 'Заполните Y и R';
        } else if (!hasX && !hasR) {
            error.innerText = 'Заполните X и R';
        } else if (!hasX) {
            error.innerText = 'Выберите значение X';
        } else if (!hasY) {
            error.innerText = 'Введите значение Y';
        } else {
            error.innerText = 'Введите значение R';
        }
        return;
    }

    try {
        validateState(state);
        error.hidden = true;
        error.innerText = '';
        redrawGraph();
    } catch (e) {
        ev.preventDefault();
        error.hidden = false;
    }
});

if (error) { error.hidden = true; }

const canvas = document.getElementById('graph');
const ctx = canvas.getContext('2d');

const width = canvas.width;
const height = canvas.height;
const R = 150;
const centerX = width / 2;
const centerY = height / 2;

canvas.addEventListener('click', function (ev) {
    const normalizedR = normalizeDecimalInput(state.r);
    const rNum = parseFloat(normalizedR);
    if (isNaN(rNum)) {
        error.hidden = false;
        error.innerText = 'R должен быть числом';
        return;
    }
    if (!(rNum > 2 && rNum < 5)) {
        error.hidden = false;
        error.innerText = 'R должен быть в строго диапазоне (2, 5)';
        return;
    }

    const rect = canvas.getBoundingClientRect();
    const localX = ev.clientX - rect.left;
    const localY = ev.clientY - rect.top;
    const px = localX - centerX;
    const py = centerY - localY;
    const scale = R / rNum;

    const modelX = px / scale;
    const modelY = py / scale;

    const xToSend = Number(modelX.toFixed(3));

    let clampedY = modelY;
    if (clampedY <= -5) clampedY = -4.999;
    if (clampedY >= 3) clampedY = 2.999;

    const form = document.getElementById('data-form');
    const base = form && form.action ? form.action : window.location.pathname;

    redrawGraph();

    const params = `?x=${encodeURIComponent(xToSend)}&y=${encodeURIComponent(clampedY)}&r=${encodeURIComponent(normalizedR)}&fromCanvas=true`;

    window.location.href = base + params;
});

function drawPointDot(x, y, r, hit = true) {
    const coords = convertToCanvasCoords(x, y, r);
    const color = hit ? '#2ecc71' : '#FF0000';
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(coords.x, coords.y, 5, 0, 2 * Math.PI);
    ctx.fill();
}

function drawHistory() {
    const rows = document.querySelectorAll('#result-table tr');
    const currentR = parseFloat(normalizeDecimalInput(state.r));

    if (isNaN(currentR)) return;

    for (let i = 1; i < rows.length; i++) {
        const cells = rows[i].children;
        if (cells.length >= 5) {
            try {
                const x = parseFloat(cells[0].innerText);
                const y = parseFloat(cells[1].innerText);
                const pointR = parseFloat(cells[2].innerText);
                const resultText = cells[4].innerText || '';
                const hit = resultText.toLowerCase().includes('попад');

                if (Math.abs(pointR - currentR) < 0.001) {
                    drawPointDot(x, y, currentR, hit);
                }
            } catch (e) {
                console.error('Error drawing point from history:', e);
            }
        }
    }
}

function convertToCanvasCoords(x, y, r) {
    const scale = R / r;
    const canvasX = centerX + (x * scale);
    const canvasY = centerY - (y * scale);
    return { x: canvasX, y: canvasY };
}

const pow10n = (exp) => {
    let result = 1n;
    for (let i = 0; i < exp; i++) {
        result *= 10n;
    }
    return result;
};

const gcdBigInt = (a, b) => {
    a = a < 0n ? -a : a;
    b = b < 0n ? -b : b;
    while (b !== 0n) {
        const temp = a % b;
        a = b;
        b = temp;
    }
    return a;
};

const decimalStringToFraction = (value) => {
    if (value === null || value === undefined) return null;
    let str = normalizeDecimalInput(value);
    if (!str) return null;
    let sign = 1n;
    if (str[0] === '-') {
        sign = -1n;
        str = str.slice(1);
    } else if (str[0] === '+') {
        str = str.slice(1);
    }
    if (!str || str === '.') return null;
    const parts = str.split('.');
    let intPart = parts[0] ?? '';
    let fracPart = parts[1] ?? '';
    if (intPart === '') intPart = '0';
    const combined = (intPart + fracPart) || '0';
    let numerator = BigInt(combined);
    if (sign === -1n) numerator = -numerator;
    let denominator = pow10n(fracPart.length);
    if (denominator === 0n) denominator = 1n;
    if (denominator === 1n) {
        return { num: numerator, den: 1n };
    }
    const divisor = gcdBigInt(numerator, denominator);
    return {
        num: numerator / (divisor === 0n ? 1n : divisor),
        den: denominator / (divisor === 0n ? 1n : divisor),
    };
};

const fractionToDecimalString = (numerator, denominator) => {
    if (denominator === 0n) return '';
    if (numerator === 0n) return '0';
    const negative = (numerator < 0n) !== (denominator < 0n);
    let num = numerator < 0n ? -numerator : numerator;
    let den = denominator < 0n ? -denominator : denominator;
    let result = (num / den).toString();
    let remainder = num % den;
    if (remainder !== 0n) {
        result += '.';
        const seenRemainders = new Set();
        while (remainder !== 0n) {
            if (seenRemainders.has(remainder)) break;
            seenRemainders.add(remainder);
            remainder *= 10n;
            const digit = remainder / den;
            result += digit.toString();
            remainder %= den;
        }
    }
    return negative ? `-${result}` : result;
};

const divideFractionByInt = (fraction, divisor) => {
    if (!fraction || !divisor) return '';
    const divisorBig = BigInt(divisor);
    let num = fraction.num;
    let den = fraction.den * (divisorBig < 0n ? -divisorBig : divisorBig);
    if (divisorBig < 0n) {
        num = -num;
    }
    const divisorGcd = gcdBigInt(num, den);
    num = divisorGcd === 0n ? num : num / divisorGcd;
    den = divisorGcd === 0n ? den : den / divisorGcd;
    return fractionToDecimalString(num, den);
};

const negateDecimalString = (value) => {
    if (!value) return '';
    return value.startsWith('-') ? value.slice(1) : `-${value}`;
};

const getAxisLabels = (rawRadius) => {
    const normalized = normalizeDecimalInput(rawRadius);
    if (!normalized || !decimalRegex.test(normalized)) return null;
    const rNum = parseFloat(normalized);
    if (isNaN(rNum) || !(rNum > 2 && rNum < 5)) return null;
    const fraction = decimalStringToFraction(normalized);
    if (!fraction) return null;
    const half = divideFractionByInt(fraction, 2);
    return {
        zero: '0',
        posHalf: half,
        posFull: normalized,
        negHalf: negateDecimalString(half),
        negFull: negateDecimalString(normalized),
    };
};

function drawBackground(r) {
    ctx.fillStyle = '#2c3e50';
    ctx.fillRect(0, 0, width, height);

    ctx.fillStyle = 'rgba(100, 181, 246, 0.3)';

    ctx.fillRect(centerX, centerY - R/2, R, R/2);

    ctx.beginPath();
    ctx.moveTo(centerX, centerY);
    ctx.lineTo(centerX + R, centerY);
    ctx.lineTo(centerX, centerY + R/2);
    ctx.closePath();
    ctx.fill();

    ctx.beginPath();
    ctx.arc(centerX, centerY, R, Math.PI, Math.PI * 0.5, true);
    ctx.lineTo(centerX, centerY);
    ctx.closePath();
    ctx.fill();

    ctx.beginPath();
    ctx.moveTo(centerX, 0);
    ctx.lineTo(centerX, height);
    ctx.moveTo(0, centerY);
    ctx.lineTo(width, centerY);
    ctx.strokeStyle = "#64B5F6";
    ctx.lineWidth = 2;
    ctx.stroke();

    ctx.strokeStyle = "rgba(100, 181, 246, 0.3)";
    ctx.lineWidth = 1;
    for (let i = 0; i <= 4; i++) {
        const offset = (R / 2) * i;
        ctx.beginPath();
        ctx.moveTo(centerX + offset, 0);
        ctx.lineTo(centerX + offset, height);
        ctx.stroke();

        ctx.beginPath();
        ctx.moveTo(centerX - offset, 0);
        ctx.lineTo(centerX - offset, height);
        ctx.stroke();

        ctx.beginPath();
        ctx.moveTo(0, centerY + offset);
        ctx.lineTo(width, centerY + offset);
        ctx.stroke();

        ctx.beginPath();
        ctx.moveTo(0, centerY - offset);
        ctx.lineTo(width, centerY - offset);
        ctx.stroke();
    }

    ctx.font = "12px monospace";
    ctx.fillStyle = "#e8f4fd";

    const labels = getAxisLabels(r);

    if (!labels) {
        ctx.fillText("0", centerX + 6, centerY - 6);
        ctx.fillText("R/2", centerX + R/2 - 6, centerY - 6);
        ctx.fillText("R", centerX + R - 6, centerY - 6);
        ctx.fillText("-R/2", centerX - R/2 - 18, centerY - 6);
        ctx.fillText("-R", centerX - R - 6, centerY - 6);
        ctx.fillText("R/2", centerX + 6, centerY - R/2 + 6);
        ctx.fillText("R", centerX + 6, centerY - R + 6);
        ctx.fillText("-R/2", centerX + 6, centerY + R/2 + 6);
        ctx.fillText("-R", centerX + 6, centerY + R + 6);
    } else {
        ctx.fillText(labels.zero, centerX + 6, centerY - 6);
        ctx.fillText(labels.posHalf, centerX + R/2 - 6, centerY - 6);
        ctx.fillText(labels.posFull, centerX + R - 6, centerY - 6);
        ctx.fillText(labels.negHalf, centerX - R/2 - 18, centerY - 6);
        ctx.fillText(labels.negFull, centerX - R - 6, centerY - 6);
        ctx.fillText(labels.posHalf, centerX + 6, centerY - R/2 + 6);
        ctx.fillText(labels.posFull, centerX + 6, centerY - R + 6);
        ctx.fillText(labels.negHalf, centerX + 6, centerY + R/2 + 6);
        ctx.fillText(labels.negFull, centerX + 6, centerY + R + 6);
    }
}

function redrawGraph() {
    drawBackground(state.r);
    drawHistory();
}

updateXState();
const rInput = document.getElementById('r');
if (rInput && rInput.value) {
    state.r = rInput.value;
    try { localStorage.setItem('savedR', state.r ?? ''); } catch (e) {}
}
const yInput = document.getElementById('y');
if (yInput && yInput.value) state.y = yInput.value;

window.addEventListener('load', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const hasFromCanvas = urlParams.has('fromCanvas');
    if (urlParams.has('r')) {
        state.r = urlParams.get('r');
        if (rInput) rInput.value = state.r;
        try { localStorage.setItem('savedR', state.r ?? ''); } catch (e) {}
    } else {
        try {
            const savedR = localStorage.getItem('savedR');
            if (savedR && (!rInput || !rInput.value)) {
                state.r = savedR;
                if (rInput) rInput.value = state.r;
            }
        } catch (e) {
        }
    }
    const yInput = document.getElementById('y');
    if (hasFromCanvas) {
        state.y = '';
        if (yInput) yInput.value = '';
        urlParams.delete('y');
        urlParams.delete('fromCanvas');
        const cleaned = urlParams.toString();
        const newUrl = cleaned ? `${window.location.pathname}?${cleaned}` : window.location.pathname;
        window.history.replaceState(null, '', newUrl);
    } else if (urlParams.has('y')) {
        state.y = urlParams.get('y');
        if (yInput) yInput.value = state.y;
    }
    if (urlParams.has('x')) {
        const xVal = urlParams.get('x');
        state.x = [xVal];
        const xSelect = document.getElementById('x-select');
        if (xSelect) xSelect.value = xVal;
    }

    redrawGraph();
});

document.getElementById("data-form").addEventListener("submit", function() {
    setTimeout(redrawGraph, 100);
});

if (window.location.search.includes("fromCanvas=true") && yInput) {
    yInput.value = '';
    state.y = '';
}

redrawGraph();

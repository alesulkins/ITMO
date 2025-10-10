"use strict";

const state = {
    x: [],
    y: 0,
    r: 1.0,
};

const table = document.getElementById("result-table");
const error = document.getElementById("error");
const possibleXs = new Set([-2, -1.5, -1, -0.5, 0, 0.5, 1, 1.5, 2]);
const possibleRs = new Set([1, 2, 3, 4, 5]);

const validateState = (state) => {
    if (!Array.isArray(state.x) || state.x.length === 0) {
        error.hidden = false;
        error.innerText = "Выберите хотя бы одно значение X";
        throw new Error("Invalid state");
    }

    for (const xVal of state.x) {
        if (!possibleXs.has(parseFloat(xVal))) {
            error.hidden = false;
            error.innerText = `X должен быть в диапазоне [${[...possibleXs].join(", ")}]`;
            throw new Error("Invalid state");
        }
    }

    const yNum = parseFloat(state.y);

    if (isNaN(yNum)) {
        error.hidden = false;
        error.innerText = "Y должен быть числом";
        throw new Error("Invalid state");
    }

    if (yNum <= -3 || yNum >= 3) {
        error.hidden = false;
        error.innerText = "Y должен быть строго в интервале (-3; 3)";
        throw new Error("Invalid state");
    }

    const rNum = parseInt(state.r);
    if (isNaN(rNum) || !possibleRs.has(rNum)) {
        error.hidden = false;
        error.innerText = `R должен быть одним из [${[...possibleRs].join(", ")}]`;
        throw new Error("Invalid state");
    }

}

const checkboxes = document.querySelectorAll('#xs input[type="checkbox"]');
checkboxes.forEach(checkbox => {
    checkbox.addEventListener('change', function(ev) {
        updateXState();
    });
});

const updateXState = () => {
    const checkedBoxes = document.querySelectorAll('#xs input[type="checkbox"]:checked');
    if (checkedBoxes.length > 0) {
        state.x = Array.from(checkedBoxes).map(cb => cb.value);
    } else {
        state.x = [];
    }
};

document.getElementById("y").addEventListener("input", (ev) => {
    state.y = ev.target.value;
});

document.getElementById("y").addEventListener("change", (ev) => {
    state.y = ev.target.value;
});

document.querySelectorAll('.r-btn').forEach(btn => {
    btn.addEventListener('click', function(ev) {
        document.querySelectorAll('.r-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        state.r = btn.getAttribute('data-value');
    });
});

document.getElementById("data-form").addEventListener("submit", async function (ev) {
    ev.preventDefault();

    validateState(state);

    const rNum = parseFloat(state.r);
    const yNum = parseFloat(state.y);
    drawScene(rNum);

    const requests = state.x.map(async (xStr) => {
        const xNum = parseFloat(xStr);
        const params = new URLSearchParams({ x: xStr, y: state.y, r: state.r });
        const response = await fetch("fcgi-proxy.php?" + params.toString());

        const entry = {
            x: xStr,
            y: state.y,
            r: state.r,
            execTime: "",
            time: "",
            result: false,
        };

        try {
            console.log(`Response for x=${xStr}: status=${response.status}, ok=${response.ok}`);
            const contentType = response.headers.get('content-type');
            console.log(`Content-Type: ${contentType}`);
            
            if (response.ok) {
                error.hidden = true;
                if (contentType && contentType.includes('application/json')) {
                    const payload = await response.json();
                    console.log(`Payload:`, payload);
                    entry.time = new Date(payload.now).toLocaleString();
                    entry.execTime = `${payload.time} нс`;
                    entry.result = payload.result ? "Попадание" : "Промах";
                } else {
                    try {
                        const text = await response.text();
                        console.log(`Non-JSON response:`, text);
                        entry.time = new Date().toLocaleString();
                        entry.execTime = "N/A";
                        entry.result = "Некорректный формат ответа";
                    } catch (textError) {
                        console.log(`Cannot read response text:`, textError);
                        entry.time = new Date().toLocaleString();
                        entry.execTime = "N/A";
                        entry.result = "Пустой ответ сервера";
                    }
                }
            } else if (response.status === 400) {
                if (contentType && contentType.includes('application/json')) {
                    const payload = await response.json();
                    console.log(`Error payload:`, payload);
                    entry.time = new Date(payload.now).toLocaleString();
                    entry.execTime = "N/A";
                    entry.result = `Ошибка: ${payload.reason}`;
                } else {
                    try {
                        const text = await response.text();
                        console.log(`Non-JSON error response:`, text);
                        entry.time = new Date().toLocaleString();
                        entry.execTime = "N/A";
                        entry.result = `Ошибка: ${text}`;
                    } catch (textError) {
                        console.log(`Cannot read error response text:`, textError);
                        entry.time = new Date().toLocaleString();
                        entry.execTime = "N/A";
                        entry.result = `Ошибка ${response.status}`;
                    }
                }
            } else {
                console.log(`Unexpected status: ${response.status}`);
                try {
                    const text = await response.text();
                    console.log(`Response text:`, text);
                    entry.time = "N/A";
                    entry.execTime = "N/A";
                    entry.result = `Ошибка ${response.status}`;
                } catch (textError) {
                    console.log(`Cannot read response text:`, textError);
                    entry.time = "N/A";
                    entry.execTime = "N/A";
                    entry.result = `Ошибка ${response.status}`;
                }
            }
        } catch (e) {
            console.error(`Exception for x=${xStr}:`, e);
            entry.time = "N/A";
            entry.execTime = "N/A";
            entry.result = "Ошибка парсинга";
        }

        const newRow = table.insertRow(1);
        const rowX = newRow.insertCell(0);
        const rowY = newRow.insertCell(1);
        const rowR = newRow.insertCell(2);
        const rowTime = newRow.insertCell(3);
        const rowExecTime = newRow.insertCell(4);
        const rowResult = newRow.insertCell(5);

        rowX.innerText = entry.x.toString();
        rowY.innerText = entry.y.toString();
        rowR.innerText = entry.r.toString();
        rowTime.innerText = entry.time;
        rowExecTime.innerText = entry.execTime;
        rowResult.innerText = entry.result;

        const prev = JSON.parse(localStorage.getItem("results") || "[]");
        localStorage.setItem("results", JSON.stringify([entry, ...prev]));

        plotPoint(xNum, yNum, rNum);
    });

    await Promise.all(requests);
});

const prevResults = JSON.parse(localStorage.getItem("results") || "[]");

prevResults.forEach(result => {
    const table = document.getElementById("result-table");

    const newRow = table.insertRow(-1);

    const rowX = newRow.insertCell(0);
    const rowY = newRow.insertCell(1);
    const rowR = newRow.insertCell(2);
    const rowTime = newRow.insertCell(3);
    const rowExecTime = newRow.insertCell(4);
    const rowResult = newRow.insertCell(5);

    rowX.innerText = result.x.toString();
    rowY.innerText = result.y.toString();
    rowR.innerText = result.r.toString();
    rowTime.innerText = result.time;
    rowExecTime.innerText = result.execTime;
    rowResult.innerText = result.result;
});

const canvas = document.getElementById('graph');
const ctx = canvas.getContext('2d');

const width = canvas.width;
const height = canvas.height;
const R = 150;
const centerX = width / 2;
const centerY = height / 2;

let currentPoint = null;

function convertToCanvasCoords(x, y, r) {
    const scale = R / r;
    const canvasX = centerX + (x * scale);
    const canvasY = centerY - (y * scale);
    return { x: canvasX, y: canvasY };
}

function drawPoint(x, y, r, showPoint = true) {
    ctx.fillStyle = '#2c3e50';
    ctx.fillRect(0, 0, width, height);

    ctx.fillStyle = 'rgba(100, 181, 246, 0.3)';

    ctx.beginPath();
    ctx.moveTo(centerX, centerY);
    ctx.arc(centerX, centerY, R, Math.PI, Math.PI * 1.5, false);
    ctx.closePath();
    ctx.fill();

    ctx.beginPath();
    ctx.rect(centerX, centerY - R, R / 2, R);
    ctx.fill();

    ctx.beginPath();
    ctx.moveTo(centerX, centerY);
    ctx.lineTo(centerX - R, centerY);
    ctx.lineTo(centerX, centerY + R);
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
    ctx.fillText("0", centerX + 6, centerY - 6);
    ctx.fillText("R/2", centerX + R / 2 - 6, centerY - 6);
    ctx.fillText("R", centerX + R - 6, centerY - 6);
    ctx.fillText("-R/2", centerX - R / 2 - 18, centerY - 6);
    ctx.fillText("-R", centerX - R - 6, centerY - 6);
    ctx.fillText("R/2", centerX + 6, centerY - R / 2 + 6);
    ctx.fillText("R", centerX + 6, centerY - R + 6);
    ctx.fillText("-R/2", centerX + 6, centerY + R / 2 + 6);
    ctx.fillText("-R", centerX + 6, centerY + R + 6);

    if (showPoint) {
        const coords = convertToCanvasCoords(x, y, r);
        ctx.fillStyle = '#FF0000';
        ctx.beginPath();
        ctx.arc(coords.x, coords.y, 4, 0, 2 * Math.PI);
        ctx.fill();
    }
}

function drawScene(r) {
    drawPoint(0, 0, r, false);
}

function plotPoint(x, y, r) {
    const coords = convertToCanvasCoords(x, y, r);
    ctx.fillStyle = '#FF0000';
    ctx.beginPath();
    ctx.arc(coords.x, coords.y, 4, 0, 2 * Math.PI);
    ctx.fill();
}

drawPoint(0, 0, 1, false);
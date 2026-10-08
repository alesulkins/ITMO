const API = '/fcgi-bin/point.jar';
const X_VALUES = [-5, -4, -3, -2, -1, 0, 1, 2, 3];
const Y_MIN = -3;
const Y_MAX = 3;
const R_MIN = 2;
const R_MAX = 5;

const canvas = document.querySelector('#plot');
const ctx = canvas.getContext('2d');
const form = document.querySelector('#point-form');
const yInput = document.querySelector('#y');
const rInput = document.querySelector('#r');
const error = document.querySelector('#error');
const tableBody = document.querySelector('#result-table tbody');
const clearButton = document.querySelector('#clear');
const serverInfo = document.querySelector('#server-info');

const SIZE = 380;
const center = SIZE / 2;
const UNIT = 30;

let palette = {};

let rows = [];

const dateFormatter = new Intl.DateTimeFormat('ru-RU', {
  dateStyle: 'short',
  timeStyle: 'medium'
});

async function callServer(options, query = '') {
  const response = await fetch(API + query, options);
  const data = await response.json();

  showServerInfo(data);

  if (!data.ok) {
    throw new Error(data.error || 'Сервер отклонил запрос.');
  }
  return data;
}

async function checkPoint(x, y, r) {
  const query = new URLSearchParams({ x, y, r });

  return callServer({ method: 'GET' }, '?' + query);
}

async function loadHistory() {
  return callServer({ method: 'GET' });
}

async function clearHistory() {
  return callServer({ method: 'DELETE' });
}

function formatExecTime(nanos) {
  if (typeof nanos !== 'number') {
    return '—';
  }

  return nanos < 1000000
    ? (nanos / 1000).toFixed(1) + ' мкс'
    : (nanos / 1000000).toFixed(2) + ' мс';
}

function showError(message) {
  error.textContent = message;
  error.dataset.visible = message ? 'true' : 'false';
  yInput.setAttribute('aria-invalid', message ? 'true' : 'false');
}

function showServerInfo(data) {
  if (!data || typeof data.currentTime !== 'number') {
    serverInfo.textContent = '';
    return;
  }

  serverInfo.innerHTML = '';

  [
    ['Время сервера', dateFormatter.format(new Date(data.currentTime))],
    ['Время работы последнего запроса', formatExecTime(data.scriptNanos)]
  ].forEach(([label, value]) => {
    const span = document.createElement('span');
    span.textContent = label + ' ';

    const b = document.createElement('b');
    b.textContent = value;
    span.appendChild(b);

    serverInfo.appendChild(span);
  });
}

function validateForm() {
  const x = Number(form.elements.x.value);
  const yText = yInput.value.trim().replace(',', '.').replace('−', '-');
  const rText = rInput.value.trim().replace(',', '.').replace('−', '-');

  if (!X_VALUES.includes(x)) {
    return { error: 'Выберите X из предложенных значений.' };
  }

  if (yText === '') {
    return { error: 'Введите Y — число строго между −3 и 3.' };
  }

  if (!/^-?\d+(\.\d+)?$/.test(yText)) {
    return { error: 'Y должен быть числом, например 1.5 или -2.' };
  }

  if (Number(yText) <= Y_MIN || Number(yText) >= Y_MAX) {
    return { error: 'Y выходит за границы: нужно строго между −3 и 3.' };
  }

  if (rText === '') {
    return { error: 'Введите R — число строго между 2 и 5.' };
  }

  if (!/^-?\d+(\.\d+)?$/.test(rText)) {
    return { error: 'R должен быть числом, например 2.5.' };
  }

  if (Number(rText) <= R_MIN || Number(rText) >= R_MAX) {
    return { error: 'R выходит за границы: нужно строго между 2 и 5.' };
  }

  return { x, y: yText, r: rText };
}

function readPalette() {
  const styles = getComputedStyle(document.body);

  ['--grid', '--accent', '--ink', '--soft', '--hit', '--miss', '--surface'].forEach(name => {
    palette[name] = styles.getPropertyValue(name);
  });
}

function toCanvasX(x) {
  return center + x * UNIT;
}

function toCanvasY(y) {
  return center - y * UNIT;
}

function drawGrid() {
  ctx.strokeStyle = palette['--grid'];
  ctx.lineWidth = 1;
  ctx.beginPath();

  for (let offset = UNIT; offset < center; offset += UNIT) {
    [center - offset, center + offset].forEach(pos => {
      ctx.moveTo(pos, 10);
      ctx.lineTo(pos, SIZE - 10);
      ctx.moveTo(10, pos);
      ctx.lineTo(SIZE - 10, pos);
    });
  }

  ctx.stroke();
}

function drawArea(r) {
  const rPx = r * UNIT;
  const halfPx = rPx / 2;

  const shapes = [
    () => ctx.rect(center - rPx, center, rPx, halfPx),

    () => {
      ctx.moveTo(center, center);
      ctx.lineTo(center, center - halfPx);
      ctx.lineTo(center + halfPx, center);
      ctx.closePath();
    },

    () => {
      ctx.moveTo(center, center);
      ctx.lineTo(center + rPx, center);
      ctx.arc(center, center, rPx, 0, Math.PI / 2);
      ctx.closePath();
    }
  ];

  ctx.save();
  ctx.fillStyle = palette['--accent'];
  ctx.strokeStyle = palette['--accent'];
  ctx.lineWidth = 1.6;

  shapes.forEach(shape => {
    ctx.globalAlpha = 0.18;
    ctx.beginPath();
    shape();
    ctx.fill();
    ctx.globalAlpha = 1;
    ctx.stroke();
  });

  ctx.restore();
}

function drawAxes(r) {
  ctx.strokeStyle = palette['--ink'];
  ctx.fillStyle = palette['--ink'];
  ctx.lineWidth = 1.2;

  ctx.beginPath();
  ctx.moveTo(16, center);
  ctx.lineTo(SIZE - 12, center);
  ctx.moveTo(center, SIZE - 16);
  ctx.lineTo(center, 12);
  ctx.stroke();

  ctx.beginPath();
  ctx.moveTo(SIZE - 6, center);
  ctx.lineTo(SIZE - 16, center - 4.5);
  ctx.lineTo(SIZE - 16, center + 4.5);
  ctx.closePath();
  ctx.fill();

  ctx.beginPath();
  ctx.moveTo(center, 6);
  ctx.lineTo(center - 4.5, 16);
  ctx.lineTo(center + 4.5, 16);
  ctx.closePath();
  ctx.fill();

  ctx.font = '500 11px "IBM Plex Mono", monospace';
  ctx.textAlign = 'left';
  ctx.textBaseline = 'alphabetic';
  ctx.fillText('x', SIZE - 16, center - 10);
  ctx.fillText('y', center + 10, 17);

  ctx.fillStyle = palette['--soft'];
  ctx.strokeStyle = palette['--soft'];
  ctx.font = '400 10px "IBM Plex Mono", monospace';

  function drawMark(pos, text, vertical) {
    ctx.beginPath();
    ctx.moveTo(vertical ? pos : center - 4, vertical ? center - 4 : pos);
    ctx.lineTo(vertical ? pos : center + 4, vertical ? center + 4 : pos);
    ctx.stroke();

    ctx.textAlign = vertical ? 'center' : 'left';
    ctx.textBaseline = vertical ? 'top' : 'middle';
    ctx.fillText(text, vertical ? pos : center + 9, vertical ? center + 9 : pos);
  }

  const marks = r === 1
    ? [[-1, '-R'], [1, 'R']]
    : [[-1, '-R'], [-0.5, '-R/2'], [0.5, 'R/2'], [1, 'R']];

  marks.forEach(([k, text]) => {
    drawMark(center + k * r * UNIT, text, true);
    drawMark(center - k * r * UNIT, text, false);
  });
}

function drawPoint(x, y, hit) {
  ctx.save();
  ctx.fillStyle = hit ? palette['--hit'] : palette['--miss'];
  ctx.strokeStyle = palette['--surface'];
  ctx.lineWidth = 2;
  ctx.beginPath();
  ctx.arc(toCanvasX(x), toCanvasY(y), 4.5, 0, Math.PI * 2);
  ctx.fill();
  ctx.stroke();
  ctx.restore();
}

function drawGraph() {
  const r = Number(rInput.value.trim().replace(',', '.').replace('−', '-'));

  readPalette();
  ctx.clearRect(0, 0, SIZE, SIZE);

  drawGrid();
  drawArea(r);
  drawAxes(r);

  rows
    .filter(row => Number(row.r) === r)
    .forEach(row => drawPoint(Number(row.x), Number(row.y), row.hit));
}

function renderTable() {
  tableBody.innerHTML = '';

  document.querySelector('#stat-total').textContent = rows.length;
  document.querySelector('#stat-hit').textContent = rows.filter(row => row.hit).length;
  document.querySelector('#stat-miss').textContent = rows.filter(row => !row.hit).length;

  rows.forEach(row => {
    const tr = document.createElement('tr');

    [
      row.x,
      row.y,
      row.r,
      dateFormatter.format(new Date(row.time)),
      formatExecTime(row.execNanos)
    ].forEach(value => {
      const td = document.createElement('td');
      td.textContent = value;
      tr.appendChild(td);
    });

    const result = document.createElement('td');
    result.dataset.state = row.hit ? 'hit' : 'miss';
    result.textContent = row.hit ? 'Попала' : 'Не попала';
    tr.appendChild(result);

    tableBody.prepend(tr);
  });
}

function apply(data) {
  rows = data.history;
  renderTable();
  drawGraph();
}

form.addEventListener('submit', async event => {
  event.preventDefault();

  const data = validateForm();

  if (data.error) {
    showError(data.error);
    yInput.focus();
    return;
  }

  try {
    apply(await checkPoint(data.x, data.y, data.r));
    showError('');
  } catch (e) {
    showError(e.message || 'Сервер недоступен, попробуйте ещё раз.');
  }
});

yInput.addEventListener('input', () => showError(''));

rInput.addEventListener('input', () => {
  showError('');
  drawGraph();
});

clearButton.addEventListener('click', async () => {
  try {
    apply(await clearHistory());
    showError('');
  } catch (e) {
    showError(e.message || 'Не удалось очистить историю.');
  }
});

drawGraph();

loadHistory()
  .then(apply)
  .catch(e => showError(e.message || 'Не удалось загрузить историю.'));

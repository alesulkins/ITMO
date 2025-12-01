<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="UTF-8" />
    <title>lab2</title>
    <link rel="stylesheet" href="style.css" />
</head>
<body>
<nav class="navbar">
    <div id="info">
        Ярулина Алесия Ильгамовна<br/>P3220, вариант 45674
    </div>
</nav>
<main class="container">
    <section class="top-section">
        <div class="input-panel">
            <form id="data-form" action="<%= request.getContextPath() %>/controller" method="GET">
                <div id="xs">
                    <label>Выберите X:</label>
                    <div class="select-group">
                        <c:set var="selX" value="${param.x}" />
                        <select name="x" id="x-select">
                            <option value="">Выберите X</option>
                            <option value="-3" <c:if test="${selX == '-3'}">selected</c:if>>-3</option>
                            <option value="-2" <c:if test="${selX == '-2'}">selected</c:if>>-2</option>
                            <option value="-1" <c:if test="${selX == '-1'}">selected</c:if>>-1</option>
                            <option value="0" <c:if test="${selX == '0'}">selected</c:if>>0</option>
                            <option value="1" <c:if test="${selX == '1'}">selected</c:if>>1</option>
                            <option value="2" <c:if test="${selX == '2'}">selected</c:if>>2</option>
                            <option value="3" <c:if test="${selX == '3'}">selected</c:if>>3</option>
                            <option value="4" <c:if test="${selX == '4'}">selected</c:if>>4</option>
                            <option value="5" <c:if test="${selX == '5'}">selected</c:if>>5</option>
                        </select>
                    </div>
                </div>

                <label for="y">Введите Y:</label>
                <input type="text" id="y" name="y" placeholder="(-5, 3)" value="${param.fromCanvas != null ? '' : (param.y != null ? param.y : requestScope.y)}" />

                <label for="r">Введите R:</label>
                <input type="text" id="r" name="r" placeholder="(2, 5)" value="${param.r != null ? param.r : requestScope.r}" />

                <button type="submit">Проверить</button>
                <div id="error" class="error-text" ${not empty requestScope.error ? '' : 'hidden'}>
                    <c:out value="${requestScope.error}" />
                </div>
            </form>
        </div>
        <div class="graph-panel">
            <canvas id="graph" width="400" height="400"></canvas>
        </div>
    </section>
    <form action="<%= request.getContextPath() %>/clear" method="post" style="margin: 15px 0; display: flex; gap: 10px; align-items: center;">
        <button type="submit" class="clear-btn">Очистить историю</button>
        <a class="clear-btn export-btn" href="<%= request.getContextPath() %>/export-pdf">Скачать отчет</a>
    </form>
    <section class="table-section">
        <table id="result-table">
            <tr>
                <th>X</th><th>Y</th><th>R</th><th>Время</th><th>Результат</th>
            </tr>
            <c:if test="${not empty results}">
                <c:forEach items="${results}" var="r">
                    <tr class="${r.hit ? 'hit-row' : 'miss-row'}">
                        <td><c:out value="${r.x}" /></td>
                        <td><c:out value="${r.y}" /></td>
                        <td><c:out value="${r.r}" /></td>
                        <td><c:out value="${r.formattedTime}" /></td>
                        <td><c:out value="${r.hit ? 'Попадание' : 'Промах'}" /></td>
                    </tr>
                </c:forEach>
            </c:if>
        </table>
    </section>

    <style>
        .clear-btn {
            padding: 8px 14px;
            background: #c0392b;
            color: white;
            border: none;
            cursor: pointer;
            border-radius: 6px;
            font-size: 14px;
            transition: .2s;
            text-decoration: none;
            display: inline-block;
        }
        .clear-btn:hover {
            background: #e74c3c;
        }
        .export-btn {
            background: #2e86de;
        }
        .export-btn:hover {
            background: #3fa1ff;
        }
    </style>

</main>
<script src="index.js" defer></script>
</body>
</html>

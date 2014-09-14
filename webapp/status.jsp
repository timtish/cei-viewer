<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="statistic" class="ru.mq.web.QueueStatistic" scope="request"/>
<jsp:useBean id="settigns" class="ru.mq.web.Settings" scope="application"/>
<%statistic.loadQueuesInfo(settigns.getQueues());%>

<html>
<head>
    <title>Queues</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <link rel="stylesheet" href="css/main.css">
</head>
<body>

<h1>MQ Statistic</h1>

<table>
    <tr>
        <th>Name</th>
        <th>Depth</th>
    </tr>
    <c:forEach items="${statistic.queuesInfo}" var="queue" varStatus="status">
         <tr ${(status.index % 2) == 1 ? 'class="alt"' : ''}>
             <td>${queue.name}</td>
             <td>${queue.depth}</td>
         </tr>
    </c:forEach>
</table>

<small>v1.0</small>

</body>
</html>
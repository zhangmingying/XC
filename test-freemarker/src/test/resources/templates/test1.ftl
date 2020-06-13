<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Hello World!</title>
</head>
<body>
Hello ${name}!
<br/>
遍历list中的学生信息（stus）
<br>
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>金额</td>
        <td>出生日期</td>
    </tr>
<#--null值处理-->
    <#if stus??>
        <#list stus as stu>
    <tr>
        <td>${stu_index}</td>
        <td <#if stu.name=='小明'>style="background: red;" </#if>>${stu.name}</td>
        <td>${stu.age}</td>
        <td>${stu.money}</td>
    <#--<td>${stu.birthday}</td>-->
    </tr>
        </#list>
    </#if>
</table>

<br>

遍历数据模型中的stuMap
<br>

<#list stuMap?keys as k>
    <tr>
        <td>${k}</td>
        <td>${(stuMap[k].name)!''}</td>
        <td>${stuMap[k].age}</td>
        <td>${stuMap[k].money}</td>
    <#--<td>${stu.birthday}</td>-->
    </tr>
</#list>
</body>
</html>
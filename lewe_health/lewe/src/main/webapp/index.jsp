<%@ page pageEncoding="utf-8"%>
<html>

<body>
<h2>欢迎使用乐为吹气检测系统！</h2>

<form id="uploadForm" method="post" action="http://192.168.5.76:8080/lewe/uploadFile/uploadImg.do" enctype="multipart/form-data">
    <input id="file" type="file" name="imageFile"/>
    <button id="upload" type="submit">上传</button>
</form>
</body>

</html>

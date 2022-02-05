$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

	$.post(
		CONTEXT_PATH+"/discuss/insert",
		{"title":title,"content":content},
		function (data){
			data = $.parseJSON(data);
			$("#hintBody").text(data.msg);
			// 设置 提示框的文本，弹出提示框，以及2s后提示框消失，页面更新
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				if(data.code==0){
					window.location.reload();
				}
			}, 2000);
		}
	)
}
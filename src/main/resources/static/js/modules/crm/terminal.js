$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'crm/terminal/list',
        datatype: "json",
        colModel: [
            { label: 'ID', name: 'id', index: "id", width: 40, key: true },
			{ label: '商户编号', name: 'merchno', width: 75},
			{ label: '商户名称', name: 'merchName', width: 45 },
            { label: '终端编号', name: 'terminalno', width: 45 },
        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
});


var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
			merchName: null,
            terminalno: null
		},
		showList: true,
		title:null,
        terminal:{
			id:null
		},
		merchs:[]
	},
	created: function () {
        $.get(baseURL + "crm/merchInfo/getMerch", function(r){
            vm.merchs = r.data;
        });
    },
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
            // vm.getMerch();
			vm.showList = false;
			vm.title = "新增";
		},
		update: function () {
            // vm.getMerch();
            var id = getSelectedRow();
            $.get(baseURL + "crm/terminal/info/"+id, function(r){
                vm.terminal = r.data;
                vm.showList = false;
                vm.title = "修改";
            });
		},
		del: function () {
			var id = getSelectedRows();
			if(id == null){
				return ;
			}
			
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "crm/terminal/delete",
                    contentType: "application/json",
				    data: JSON.stringify(id),
				    success: function(r){
						if(r.code == 0){
							alert('操作成功', function(){
								vm.reload();
							});
						}else{
							alert(r.msg);
						}
					}
				});
			});
		},
        // getMerch: function(){
         //    $.get(baseURL + "crm/merchInfo/getMerch", function(r){
         //        vm.merchs = JSON.parse(r.data);
         //    });
		// },
		saveOrUpdate: function () {
			var url = vm.terminal.id == null ? "crm/terminal/save" : "crm/terminal/update";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.terminal),
			    success: function(r){
			    	if(r.code === 0){
						alert('操作成功', function(){
							vm.reload();
						});
					}else{
						alert(r.msg);
					}
				}
			});
		},
	    reload: function () {
	    	vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                postData:{'merchName': vm.q.merchName,'terminalno': vm.q.terminalno},
                page:page
            }).trigger("reloadGrid");
		}
	}
});
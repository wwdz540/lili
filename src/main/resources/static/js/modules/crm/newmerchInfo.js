$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'crm/newmerchInfo/list',
        datatype: "json",
        colModel: [
            { label: '商户ID', name: 'id', index: "id", width: 40, key: true },
            { label: '商户名称', name: 'name', width: 75,formatter:function (v,o,merch) {
					return v + "&nbsp&nbsp;<span style='color: #00a7d0'>["+mtype[merch.deptType]+"]</span>";
                } },

			{ label: '商户编号', name: 'merchno', width: 75},
            { label: '盛大商户编号', name: 'merchnoSub', width: 45 },
            { label: '父商户名称', name: 'parentName', width: 45 },
             { label: '客户姓名', name: 'legalName', width: 45 },
            // { label: '手机号码', name: 'mobile', width: 45 },
            // { label: '商户类型', name: 'industry', width: 45 },
			{ label: '地址', name: 'address', index: "address", width: 100},
			{ label: '操作',name:'id',width: 45,formatter:function (v,o,merch) {
					var opStr ="";
					switch (merch.deptType)
					{

						case 1:
							break;
						case 2:
						case 3:
                            opStr = "<a href='###' onclick='vm.addChild("+merch.id+","+merch.deptType+" ,\""+merch.name+"\")'> 添加子商铺</a>";
                            break;
						default:
							break;

					}
					return opStr;
                }}
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

var mtype={1:"商铺",2:"代理商",3:"企业集团",4:"代理子帐户",5:"企业子帐户"};


var vm = new Vue({
	el:'#rrapp',
	data:{
        defaultRate:[
            { payType:'支付宝',rate:0.03,max:25,shareBenefit:0.02},
            { payType:'微信',rate:0.03,max:25,shareBenefit:0.02},
            { payType:'借记卡',rate:0.03,max:25,shareBenefit:0.02},
            { payType:'贷记卡',rate:0.03,max:25,shareBenefit:0.02},
            { payType:'标准',rate:0.03,max:25,shareBenefit:0.02}
        ],
		q:{
            keyword:null
		},
		showList: true,
		title:null,
		users:[],
		merch:{
			id:null,
            deptType:null,
            parentId:1,
			rateConfigs:this.defaultRate
		}
	},
    created: function () {

    },
	methods: {
		query: function () {
			vm.reload();
		},
		add: function(){
			vm.showList = false;
			vm.title = "新增";
			vm.merch.deptType = null;
			vm.merch.parentId = 1;
			vm.merch.rateConfigs = vm.defaultRate;

		},
		addChild:function(parentId,dtype,parentName){

            vm.title = "新增["+parentName+"]商铺户";
            vm.merch.parentId = parentId;
            switch(dtype){
				case 1:
                    vm.merch.rateConfigs = vm.defaultRate;
					break;
				case 2:
                    vm.merch.rateConfigs = vm.defaultRate;
					vm.merch.deptType = 4;
					break;
				case 3:
					console.log("case 3 case 3")
					vm.merch.deptType = 5;
                    vm.merch.rateConfigs = null;
                    break;
				default:
                    vm.merch.rateConfigs = vm.defaultRate;
					vm.merch.deptType = null;
					break;
            }
            vm.showList = false;
            console.log(dtype)
			console.log(vm.merch.deptType)
			console.log(vm.merch.rateConfigs)
        },
		update: function () {
            var merchId = getSelectedRow();
            $.get(baseURL + "crm/merchInfo/info/"+merchId, function(r){
                vm.merch = r.data;
                vm.showList = false;
                vm.title = "修改";
            });
		},
		del: function () {
			var merchIds = getSelectedRows();
			if(merchIds == null){
				return ;
			}
			
			confirm('确定要删除选中的记录？', function(){
				$.ajax({
					type: "POST",
				    url: baseURL + "crm/merchInfo/delete",
                    contentType: "application/json",
				    data: JSON.stringify(merchIds),
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
		getRole: function(merchId){
            $.get(baseURL + "crm/merchInfo/info/"+merchId, function(r){
            	vm.merch = r.data;
				vm.showList = false;
    		});
		},
		saveOrUpdate: function () {
			var url = vm.merch.id == null ? "crm/newmerchInfo/save" : "crm/newmerchInfo/update";
			$.ajax({
				type: "POST",
			    url: baseURL + url,
                contentType: "application/json",
			    data: JSON.stringify(vm.merch),
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
                postData:{'merchName': vm.q.merchName,'merchno': vm.q.merchno},
                page:page
            }).trigger("reloadGrid");
		}
	}
});
var mavg = 0 ;
$(function () {

    $("#jqGrid").jqGrid({
        url: baseURL + 'crm/transData/list',
        datatype: "json",
        colModel: [
            { label: 'ID', name: 'id', width: 35 },
			{ label: '订单号', name: 'orderId', width: 40},
            { label: '终端流水号', name: 'traceNo', width: 40 },
			{ label: '金额', name: 'amt', width: 40 },
            { label: '分润', name: 'sharePoint', width: 40 },
            { label: '商户号', name: 'merchantId', width: 55 },
            { label: '商户名称', name: 'merchName', width: 55 },
            { label: '交易时间', name: 'txnDatetime', width: 55 },
            { label: '交易参考号', name: 'txnRef', width: 40 },
			{ label: '终端号', name: 'terminalId', width: 40 },
            { label: '卡号末4位', name: 'shortPan', width: 30 },
            { label: '交易状态', name: 'respCode', width: 40 },
            { label: '发卡机构', name: 'issuerCode', width: 30 },
            { label: '卡类型', name: 'cardType', width: 30},
            { label: '客单价', name: 'cardType', width: 30,formatter:fmtFun}

        ],
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: false,
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


            $.ajax({
                type: "get",
                url: baseURL + "crm/transData/sharePoint",
                contentType: "application/json",
                data: {'merchName': vm.q.merchName,'leaderName': vm.q.leaderName,'dateStart': vm.q.dateStart,'dateEnd': vm.q.dateEnd},
                // data: vm.q,
                success: function(r){
                    $("#sharpoint").html("总金额："+r.amount+",分润："+r.sharePoint);
                    // vm.amount = r.amount;
                    // vm.sharePoint = r.sharePoint;
                }
            });
        },
        loadComplete: function (data){

            if(data.avg) {
              //  mavg = data.avg;
                $(".avgc").html(data.avg);
            }
        }
    });
    vm.getDept();
});

var  fmtFun= function (a,b,c) {
    return "<span class='avgc'></span>";
}


var setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "deptId",
            pIdKey: "parentId",
            rootPId: -1
        },
        key: {
            url:"nourl"
        }
    }
};
var ztree;


var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
			merchName: "",
            leaderName:"",
            dateStart:"",
            dateEnd:"",
            dateRange:"",
            deptId:0,
            issuerCode:""
		},
        amount:0,
        sharePoint:0,
        showList:true,
		transData:{
			id:null
		},
        queryDeptName:""
	},
	methods: {


		query: function () {
			vm.reload();
		},
		getRole: function(id){
            $.get(baseURL + "crm/transData/info/"+id, function(r){
            	vm.transData = r.data;
				vm.showList = false;
    		});
		},
        excel: function(){
		    window.location.href=baseURL + "crm/transData/excel?token="+token+"&dateStart="+vm.q.dateStart+"&dateEnd="+vm.q.dateEnd;
        },
	    reload: function () {
	    	vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                postData:vm.q,
                page:page
            }).trigger("reloadGrid");
		},
        initDate:function () {
            var locale = {
                "format": 'YYYY-MM-DD',
                "separator": " - ",
                "applyLabel": "确定",
                "cancelLabel": "取消",
                "fromLabel": "起始时间",
                "toLabel": "结束时间'",
                "customRangeLabel": "自定义",
                "weekLabel": "W",
                "daysOfWeek": ["日", "一", "二", "三", "四", "五", "六"],
                "monthNames": ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
                "firstDay": 1
            };
            //初始化
            $('#daterange-btn').daterangepicker({
                'locale': locale,
                ranges: {
                    // '今日': [moment(), moment()],
                    '昨日至天': [moment().subtract(1, 'days'), moment()],
                    '最近7日': [moment().subtract(6, 'days'), moment()],
                    '最近30日': [moment().subtract(29, 'days'), moment()],
                    '本月': [moment().startOf('month'), moment().endOf('month')],
                    '上月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
                },
                // startDate: moment().subtract(0, 'days'),
                startDate: moment(),
                endDate: moment()
            },
            function (start, end) {
                vm.q.dateStart = start.format('YYYY-MM-DD');
                vm.q.dateEnd = end.format('YYYY-MM-DD');
            });
        },

        getDept: function(){
            //加载部门树
            $.get(baseURL + "sys/dept/list", function(r){
                ztree = $.fn.zTree.init($("#deptTree"), setting, r);
                var node = ztree.getNodeByParam("deptId", userInfo.deptId);
                if(node != null){
                    ztree.selectNode(node);

                    vm.queryDeptName = node.name;
                }
            })
        },

        deptTree: function(){

            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择部门",
                area: ['300px', '450px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#deptLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = ztree.getSelectedNodes();


                    if(node[0].deptId == 1)
                    {
                        vm.q.deptId = 0;
                        vm.q.parentId = 0;
                    }
                    else if(node[0].deptType == 2 || node[0].deptType == 3 ){
                        vm.q.deptId = 0;
                        vm.q.parentId = node[0].deptId;
                    }else{

                        vm.q.deptId = node[0].deptId;
                        vm.q.parentId = 0;
                    }


                    //选择上级部门
                    // vm.user.deptId = node[0].deptId;
                    // vm.user.deptName = node[0].name;
                    vm.queryDeptName =  node[0].name;

                    layer.close(index);
                }
            });
        }
	},
    mounted:function () {
        this.initDate();

    }
});
$(function () {

    var col =  [
        { label: 'ID', name: 'id', width: 35 },
      //  { label: '订单号', name: 'orderId', width: 40},
       // { label: '终端流水号', name: 'traceNo', width: 40 },
        { label: '金额', name: 'amt', width: 40 },
        { label: '分润', name: 'shareBenefit', width: 40 },
        { label: '商户号', name: 'merchantId', width: 55 },
        { label: '商户名称', name: 'merchName', width: 55 },
        { label: '交易时间', name: 'txnDatetime', width: 55 },
        { label: '交易参考号', name: 'txnRef', width: 40 },
        { label: '终端号', name: 'terminalId', width: 40 },
        { label: '卡号末4位', name: 'shortPan', width: 30 },
        { label: '交易状态', name: 'respCode', width: 40 },
        { label: '发卡机构', name: 'issuerCode', width: 30 },
        { label: '卡类型', name: 'cardType', width: 30},
        {label:'费率%',name:'tdRate',width:30},
        {label:'手续费',name:'serviceCharge',width:30},
        { label: '客单价', name: 'cardType', width: 40,formatter:fmtFun}

    ];
    if(!(userInfo.deptType == 0 || userInfo == 1)){ //商户没有分润
        col = col.filter(function (value) {
            return value.label != '分润';
        })
    }

    $("#jqGrid").jqGrid({
        url: baseURL + 'crm/td/list',
        datatype: "json",
        postData:vm.q,
        colModel: col,
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
        //	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });
            $.ajax({
                type: "get",
                url: baseURL + "crm/td/summary",
                contentType: "application/json",
                data: vm.q,
                success: function(r){
                    $("#sharpoint").html("总金额："+r.amount+",分润："+r.sharePoint);
                }
            });
        },
        loadComplete: function (data){
            if(data.avg) {
                $(".avgc").html(data.avg);
            }
        }
    });

});

var  fmtFun= function (a,b,c) {
    return "<span class='avgc'></span>";
}


var setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "mcId",
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
            ds:"fy",
            issuerCode:"",
            payType:""
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

        excel: function(){
		    window.location.href=baseURL + "crm/td/excel?ds="+vm.q.ds+"&token="+token+"&dateStart="+vm.q.dateStart+"&dateEnd="+vm.q.dateEnd;
        },
	    reload: function () {
            console.log("=="+vm.q.ds);
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
                    '昨日至现在':[moment().subtract(1, 'days'),moment().add(1,'days')],
                    '昨日至今天': [moment().subtract(1, 'days'), moment()],
                    '最近7日': [moment().subtract(6, 'days'), moment()],
                    '最近30日': [moment().subtract(29, 'days'), moment()],
                    '本月': [moment().startOf('month'), moment().endOf('month')],
                    '上月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
                },
                // startDate: moment().subtract(0, 'days'),
                startDate: moment().subtract(1, 'days'),
                endDate: moment()
            },
            this.onDateChange);
        },
        onDateChange: function(start, end){
            vm.q.dateStart = start.format('YYYY-MM-DD');
            vm.q.dateEnd = end.format('YYYY-MM-DD')
            console.log(vm.q.dateStart);
            console.log(vm.q.dateEnd);
            vm.q.dateRange = vm.q.dateStart + " - " +vm.q.dateEnd;
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
                type:  1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择商户",
                area: ['300px', '450px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#deptLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = ztree.getSelectedNodes();
                    if(node[0].mcId != 1)
                    {
                        vm.q.path=node[0].path;
                    }
                    //选择上级部门

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
vm.getDept();
vm.onDateChange(moment().subtract(1, 'days'),moment());
$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'crm/sharePoint/list',
        datatype: "json",
        colModel: [
            { label: 'ID', name: 'userId', width: 35 },
			{ label: '名称', name: 'userName', width: 40},
            { label: '总金额', name: 'amount', width: 40 },
            { label: '分润', name: 'sharePoint', width: 40 },
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
        }
    });
});


var vm = new Vue({
	el:'#rrapp',
	data:{
		q:{
            dateStart:"",
            dateEnd:"",
            dateRange:""
		},
        showList:true,
		transData:{
			id:null
		}
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
	    reload: function () {
	    	vm.showList = true;
			var page = $("#jqGrid").jqGrid('getGridParam','page');
			$("#jqGrid").jqGrid('setGridParam',{ 
                postData:{'dateStart': vm.q.dateStart,'dateEnd': vm.q.dateEnd},
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
                    '今日': [moment(), moment()],
                    '昨日': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
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
        }
	},
    mounted:function () {
        this.initDate();

    }
});
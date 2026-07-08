<!--地区组件文本-->
<template>
  　　<span>{{officeName}}</span>
</template>
<script>
  export default {
    name: 'office-span',
    props: {
      separator: {
        type: String,
        default: "/"
      },
      value: {
        type: String,
        default: ""
      }
    },
    data() {
      return {
        officeName: "",
        officeData: [],
        officeList: [],
      }
    },
    mounted() {
      this.getSysOfficeList();
    },
    watch: {
      value(value) {
        this.officeData = [];
        this.getOfficeName(value)
      },
    },
    methods: {
      getOfficeName(value) {
        const that = this;
        for (let i = 0; i < that.officeList.length; i++) {
          let item = that.officeList[i];
          if (item.id == value) {
            that.officeData.push({name: item.name});
            if (item.pid) {
              that.getOfficeName(item.pid)
            }
            break
          }
        }
        //递归结束后拼接名称
        that.officeName = that.officeData.map(function (v) {
          return v.name;
        }).reverse().join(that.separator);
      },
      //【所有部门】
      getSysOfficeList() {
        this.$request.doPost('/admin/sysOffice/getList').then(res => {
          if (res.isOk) {
            this.officeList = res.list.filter(function (v) {
              return v.code != 'root' && v.type != '1'
            });
            //等数据加载后读取
            this.getOfficeName(this.value);
          }
        })
      },
    }
  }
</script>

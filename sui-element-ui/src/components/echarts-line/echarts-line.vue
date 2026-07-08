<!--柱状图-->
<template>
  <div :id="id" :style="style"></div>
</template>
<script>
  export default {
    name: 'echarts-line',
    props: {
      id: {
        type: String,
        default: ""
      },
      style: {
        type: Object,
        default: {width: '100%', height: '500px'}
      },
      title: {
        type: String,
        default: "柱状图"
      },
      data: {
        type: Object,
        default: {}
      },
      // xData: {//['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
      //   type: Array,
      //   default: []
      // },
      // yData: {//[120, 200, 150, 80, 70, 110, 130],
      //   type: Array,
      //   default: []
      // },
    },
    watch: {
      data: {
        handler(newMap, oldMap) {
          this.init(newMap)
        },
        deep: true
      }
    },
    mounted() {
      //this.init(this.data);
    },
    methods: {
      init(data) {
        // 基于准备好的dom，初始化echarts实例
        let lineChart = this.$echarts.init(document.getElementById(this.id));
        // 绘制图表
        let option = {
          title: {text: this.title, left: 'center'},
          tooltip: {},
          xAxis: {
            data: data.xData
          },
          yAxis: {},
          series: [{
            type: 'bar',
            data: data.yData,
            itemStyle: {        //上方显示数值
              normal: {
                label: {
                  show: true, //开启显示
                  position: 'top', //在上方显示
                  textStyle: { //数值样式
                    color: 'gray',
                    fontSize: 16
                  }
                },
                color: function (params) {
                  let colorList = ['#C23531', '#2F4554', '#61A0A8', '#D48265', '#91C7AE', '#749F83', '#CA8622', '#BDA29A', '#6E7074', '#546570'];
                  let colorItem = colorList[params.dataIndex];
                  return colorItem;
                }
              }
            }
          }]
        };
        lineChart.setOption(option);
      }
    }
  }
</script>

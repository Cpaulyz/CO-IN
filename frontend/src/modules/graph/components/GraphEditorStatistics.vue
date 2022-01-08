<template>
  <div>
    <el-button
      type="text"
      size="small"
      icon="el-icon-arrow-left"
      @click="exitStatistics"
      >返回</el-button
    >
    <div id="statistics"></div>
  </div>
</template>

<script>
export default {
  name: 'GraphEditorStatistics',
  props: {
    statisticsData: {
      type: Array,
    },
  },
  data() {
    return {
      container: null,
      chart: null,
    };
  },
  watch: {
    statisticsData() {
      console.log('draw statisticsData', this.statisticsData);
      this.draw();
    },
  },
  methods: {
    draw() {
      const option = {
        title: {
          text: '实体类型统计',
          subtext: '各类实体总数',
          left: 'center',
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)',
        },
        legend: {
          top: 'bottom',
        },
        toolbox: {
          show: true,
          feature: {
            mark: { show: true },
            dataView: { show: true, readOnly: true },
            restore: { show: true },
            saveAsImage: { show: true },
          },
        },
        series: [
          {
            name: '实体类型',
            type: 'pie',
            radius: [15, 90],
            center: ['50%', '50%'],
            roseType: 'area',
            itemStyle: {
              borderRadius: 8,
            },
            data: this.statisticsData,
          },
        ],
      };
      this.chart.setOption(option);
    },
    exitStatistics() {
      this.$emit('exit');
    },
  },
  mounted() {
    const container = document.querySelector('#statistics');
    const chart = this.$echarts.init(container);
    this.container = container;
    this.chart = chart;
    this.draw();
  },
};
</script>

<style scoped>
#statistics {
  width: 300px;
  height: 400px;
}
</style>

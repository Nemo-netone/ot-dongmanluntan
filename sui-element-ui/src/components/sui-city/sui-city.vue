<!--地区组件-->
<template>
  <el-cascader ref="cityCascader"
               :options="CityInfo"
               v-model="cityId"
               :change-on-select="true"
               :filterable="true"
               :placeholder="placeholder"
               :disabled="disabled"
               :clearable="clearable"
               @change="handleChange"
               :value="cityId"
               :props="{label:'label',value:'value',expandTrigger: 'hover',children:'children' }">
  </el-cascader>
</template>

<script>
  import city from "./city"
  export default {
    name: 'sui-city',
    props: {
      cityId: {
        type: String,
        default: ''
      },
      placeholder: {
        type: String,
        default: ''
      },
      disabled: {
        type: Boolean,
        default: false
      },
      clearable: {
        type: Boolean,
        default: false
      }
    },
    model: {
      prop: 'cityId',
      event: 'cityIdBind'
    },
    data() {
      return {
        CityInfo: city.areaList,
      }
    },
    methods: {
      handleChange(value) {
        let pid = this.$refs.cityCascader.getCheckedNodes()[0].value;
        this.$emit('cityIdBind', pid);
      },
    }
  }
</script>

<template>
<span>
  <span :style="item.style" v-for="item in dict" :key="item.dictValue">
    <span v-if="item.dictValue==value">{{item.dictLabel}}</span>
  </span>
</span>
</template>
<script>
  export default {
    props: {
      dict: {
        type: Array,
        default: []
      },
      value: {
        type: String,
        default: ''
      }
    },
  };
</script>

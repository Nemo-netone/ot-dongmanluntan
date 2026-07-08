<!--地区组件文本-->
<template>
　　<span>{{cityName}}</span>
</template>
<script>
  import city from "./city"
  export default {
    name: 'city-span',
    props: {
      separator: {
        type: String,
        default: "/"
      },
      value: {
        type: String,
        default: ""
      },
    },
    data() {
      return {
        CityInfo: city.areaList,
      }
    },
    computed: {
      cityName() {
        let slotsText = this.value;
        if (slotsText.length == 2) {
          return this.myAddressCity(slotsText)
        }
        if (slotsText.length == 4) {
          return this.myAddressCity(slotsText.substring(0, 2)) + this.separator + this.myAddressArea(slotsText.substring(0, 4))
        }
        if (slotsText.length == 6) {
          return this.myAddressCity(slotsText.substring(0, 2)) + this.separator + this.myAddressArea(slotsText.substring(0, 4)) + this.separator + this.myAddressMinarea(slotsText.substring(0, 6))
        }
      },
    },
    methods: {
      myAddressCity: function (value) {
        let cityInfo = this.CityInfo;
        for (let y in cityInfo) {
          if (this.CityInfo[y].value == value) {
            return this.CityInfo[y].label
          }
        }
      },
      myAddressArea: function (value) {
        for (let y in this.CityInfo) {
          for (let z in this.CityInfo[y].children) {
            if (this.CityInfo[y].children[z].value == value && value != undefined) {
              return this.CityInfo[y].children[z].label;
            }
          }
        }
      },
      myAddressMinarea: function (value) {
        for (let y in this.CityInfo) {
          for (let z in this.CityInfo[y].children) {
            for (let i in this.CityInfo[y].children[z].children) {
              if (this.CityInfo[y].children[z].children[i].value == value && value != undefined) {
                return this.CityInfo[y].children[z].children[i].label
              }
            }
          }
        }
      },
    }
  }
</script>

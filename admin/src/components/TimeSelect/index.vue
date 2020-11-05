<template>
  <div>
    <el-time-select
      placeholder="起始时间"
      v-model="startTime"
      :picker-options="{
      start: '00:00',
      step: '01:00',
      end: '24:00'
    }">
    </el-time-select>
    <el-time-select
      placeholder="结束时间"
      v-model="endTime"
      :picker-options="{
       start: '00:00',
      step: '01:00',
      end: '24:00',
      minTime: startTime
    }">
    </el-time-select>
  </div>
</template>

<script>
  export default {
    name: "index",
    data() {
      return {
        startTime: '',
        endTime: ''
      }
    },
    props: {
      value: {}
    },
    beforeMount(){
      // 接收 v-model 数据
      if(this.value){
        this.startTime = this.value.split(',')[0]
        this.endTime = this.value.split(',')[1]
      }
    },
    watch: {
      startTime: function(val) {
        this.$emit('input', [val, this.endTime].join(','))
      },
      endTime: function(val) {
        this.$emit('input', [this.startTime, val].join(','))
      }
    }
  }
</script>

<style scoped>

</style>

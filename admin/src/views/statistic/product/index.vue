<template>
  <div>
    <product></product>
    <table-data></table-data>
  </div>
</template>

<script>
import product from './components/visualization';
import tableData from './components/tableData';
export default {
  components: {
    product,
    tableData,
  },
};
</script>

<style lang="sass" scoped></style>

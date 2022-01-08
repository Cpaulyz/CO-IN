<template>
  <img
    src="../assets/coin.png"
    alt="coin logo"
    :width="`${width || 200}px`"
    @click="$emit('click', $event)"
  />
</template>

<script>
export default {
  name: 'Logo',
  props: ['width'],
};
</script>

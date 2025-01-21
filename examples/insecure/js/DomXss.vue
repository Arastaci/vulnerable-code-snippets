<script setup>
import PageContent from './components/PageContent.vue';
</script>

<template>
  <main>
    <img src="@/assets/logo.svg" class="logo">
    <PageContent
      msg="You did it!"
      v-bind="$route.query"
      class="card-lg"
    />
  </main>
</template>

<style>
@keyframes spinner {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
img.logo {
  animation: 5s linear spinner infinite;
}
</style>
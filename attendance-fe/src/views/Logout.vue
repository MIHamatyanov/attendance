<template>
    <div></div>
</template>

<script>
export default {
    name: 'Logout',

    created() {
        this.$store.dispatch('logout');
        this.$router.push({name: 'Login'});
    }
}
</script>
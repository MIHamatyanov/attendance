<template>
    <div class="main_container">
        <v-app-bar
            absolute
            color="#003b73"
            elevate-on-scroll
            scroll-target="#scroll"
            height="100px"
        >
            <v-row no-gutters>
                <LogoIcon class="ml-12"/>
            </v-row>
        </v-app-bar>
        <v-row style="height: 100vh" align="center" no-gutters id="scroll">
            <div v-if="error">error</div>
            <v-row no-gutters justify="center">
                <v-col cols="4">
                    <v-card class="py-12" style="border: 1px solid #003b73;">
                        <v-row no-gutters>
                            <v-col cols="12" class="mt-n5 mb-5 d-flex justify-space-around">
                                <v-avatar
                                    color="white"
                                    size="150"
                                >
                                    <AvatarIcon/>
                                </v-avatar>
                            </v-col>
                        </v-row>
                        <v-row no-gutters>
                            <v-col cols="12" class="d-flex justify-space-around">
                                <span class="login_label">Email</span>
                            </v-col>
                            <v-col cols="12">
                                <v-text-field
                                    class="mx-15"
                                    v-model="email"
                                ></v-text-field>
                            </v-col>
                        </v-row>
                        <v-row no-gutters class="mt-7">
                            <v-col cols="12" class="d-flex justify-space-around">
                                <span class="login_label">Пароль</span>
                            </v-col>
                            <v-col cols="12">
                                <v-text-field
                                    class="mx-15"
                                    v-model="password"
                                    @keyup.enter="login"
                                    type="password"
                                ></v-text-field>
                            </v-col>
                        </v-row>
                        <v-row no-gutters align="center" justify="center">
                            <v-btn
                                class="mt-4 py-5"
                                @click="login"
                                width="200px"
                                color="#003b73"
                                dark
                            >
                                Войти
                            </v-btn>
                        </v-row>
                    </v-card>
                </v-col>
            </v-row>
        </v-row>
    </div>
</template>

<script>
import LogoIcon from "../components/icons/LogoIcon";
import AvatarIcon from "../components/icons/AvatarIcon";
export default {
    name: "Login",
    components: {AvatarIcon, LogoIcon},
    data() {
        return {
            email: '',
            password: '',
            loading: false,
            error: false
        }
    },

    methods: {
        async login() {
            this.loading = true;
            let user = {
                email: this.email,
                password: this.password
            }

            this.loading = true;
            let error = await this.$store.dispatch('authorization', user);
            if (error) {
                this.error = true;
            }
            this.loading = false;
        }
    }
}
</script>

<style scoped>
.login_label {
    font-size: 18px;
    color: #003b73;
}
</style>
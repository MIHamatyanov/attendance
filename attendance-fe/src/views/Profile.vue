<template>
    <div class="main_container">
        <v-app-bar
            fixed
            color="#003b73"
            elevate-on-scroll
            height="100px"
        >
            <v-row no-gutters class="px-12">
                <LogoIcon/>
                <div class="header_link_wrapper mt-3">
                    <a v-for="i in user.group.course" class="mr-5 header_link" :key="i" @click="loadSubjects(i)">{{i}} курс</a>
                </div>
                <v-spacer></v-spacer>
                <router-link :to="{name: 'Profile'}">
                    <v-avatar
                        size="45"
                    >
                        <AvatarIconWhite/>
                    </v-avatar>
                </router-link>
                <a href="/logout" class="ml-5 mt-3 logout_btn">
                    Выход
                </a>
            </v-row>
        </v-app-bar>
        <v-row no-gutters style="margin-top: 100px;">
            <v-col v-if="$vuetify.breakpoint.mdAndUp" cols="1" md="3" lg="2">
                <NavigationMenu/>
            </v-col>
            <v-col cols="10" class="pl-12">
                <v-row no-gutters class="mt-9">
                    <v-col cols="2">
                        <a>
                            <v-img
                                :src="photoSrc === '' ? '../assets/addPhoto.png' : photoSrc"
                                height="295"
                                width="250"
                                class="profile_photo"
                                @click="changePhoto"
                            ></v-img>
                        </a>
                    </v-col>
                    <v-col cols="5" class="ml-9">
                        <div class="block_title_wrapper block_main">
                            <span class="block_title">Общие сведения</span>
                            <v-spacer></v-spacer>
                            <!--                            <a href="">Сменить пароль</a>-->
                        </div>
                        <v-row no-gutters class="mt-7">
                            <v-col cols="12">
                                <span>ФИО:</span>
                                <span
                                    class="ml-3">
                                    {{ user.surname + ' ' + user.name + ' ' + user.patronymic }}</span>
                            </v-col>
                        </v-row>
                        <v-row no-gutters class="mt-7">
                            <v-col cols="12">
                                <span>Дата рождения:</span>
                                <span class="ml-3">{{ new Date(user.birthDate).toLocaleDateString("ru-RU") }}</span>
                            </v-col>
                        </v-row>
                        <v-row no-gutters class="mt-7">
                            <v-col cols="12">
                                <span>Гражданство:</span>
                                <span class="ml-3">{{ user.citizenship }}</span>
                            </v-col>
                        </v-row>
                        <v-row no-gutters class="mt-7">
                            <v-col cols="12">
                                <span>Институт (факультет):</span>
                                <span class="ml-3">{{ user.institute }}</span>
                            </v-col>
                        </v-row>
                        <v-row no-gutters class="mt-7">
                            <v-col cols="12">
                                <span>№ группы:</span>
                                <span class="ml-3">{{ user.group.name }}</span>
                            </v-col>
                        </v-row>
                    </v-col>
                </v-row>
                <v-row no-gutters class="mt-9">
                    <v-row no-gutters>
                        <v-col cols="7">
                            <div class="block_title_wrapper block_password">
                                <span class="block_title">Смена пароля</span>
                            </div>
                        </v-col>
                    </v-row>
                </v-row>
                <v-row no-gutters class="mt-5">
                    <v-col cols="2">
                        <div class="field-label">Старый пароль</div>
                        <v-text-field
                            class="mt-1 password_field"
                            :class="$vuetify.breakpoint.smAndDown ? 'text-field' : ''"
                            :flat="true"
                            type="password"
                            v-model="oldPassword"
                            outlined
                        ></v-text-field>
                    </v-col>
                </v-row>
                <v-row no-gutters class="mt-n3">
                    <v-col cols="2">
                        <div class="field-label">Новый пароль</div>
                        <v-text-field
                            class="mt-1"
                            :class="$vuetify.breakpoint.smAndDown ? 'text-field' : ''"
                            :flat="true"
                            v-model="newPassword"
                            type="password"
                            outlined
                        ></v-text-field>
                    </v-col>
                </v-row>
                <v-row no-gutters class="mt-n3">
                    <v-col cols="2">
                        <div class="field-label">Повтор пароля</div>
                        <v-text-field
                            class="mt-1"
                            :class="$vuetify.breakpoint.smAndDown ? 'text-field' : ''"
                            :flat="true"
                            v-model="repeatPassword"
                            type="password"
                            outlined
                        ></v-text-field>
                    </v-col>
                </v-row>

                <v-row no-gutters>
                    <v-col cols="2">
                        <v-btn :disabled="passwordDisabled" class="save_btn" color="#003b73" @click="changePassword">Сохранить</v-btn>
                    </v-col>
                </v-row>
            </v-col>
        </v-row>
    </div>
</template>

<script>
import LogoIcon from "../components/icons/LogoIcon";
import NavigationMenu from "../components/NavigationMenu";
import AvatarIconWhite from "../components/icons/AvatarIconWhite";

export default {
    name: "Profile",
    components: {AvatarIconWhite, NavigationMenu, LogoIcon},
    data() {
        return {
            user: {},
            photoSrc: '',
            oldPassword: '',
            newPassword: '',
            repeatPassword: '',
            subjects: []
        }
    },

    computed: {
        passwordDisabled() {
            return this.oldPassword === '' || this.newPassword === '' || this.repeatPassword === '' || this.newPassword !== this.repeatPassword;
        }
    },

    async created() {
        await this.$store.dispatch('loadCurrentUserData');
        this.user = Object.assign({}, this.$store.getters.currentUser);
        if (this.user.photoUrl) {
            let photoResponse = await this.$store.dispatch("getByteArray", this.user.photoUrl);
            if (photoResponse.success) {
                this.photoSrc = photoResponse.data;
            }
        }
    },

    methods: {
        async loadSubjects(course) {
            this.$route.params.course = course;
            this.$router.replace({name: 'Profile', query: {course: course}}).catch(() => {});
        },

        async changePassword() {
            let data = {
                oldPassword: this.oldPassword,
                newPassword: this.newPassword
            };

            let response = await this.$store.dispatch("changePassword", data);
            if (response.success) {
                this.oldPassword = '';
                this.newPassword = '';
                this.repeatPassword = '';
            } else if (response.data.response.status === 403) {
                alert('Error');
            }
        },

        async changePhoto() {
            let input = document.createElement('input');
            input.type = 'file';
            input.accept = "image/jpeg, image/png";

            input.onchange = e => {
                let file = e.target.files[0];

                if (file.type === 'image/jpeg' || file.type === 'image/png') {
                    let formData = new FormData();
                    formData.append('file', file);
                    this.uploadFile(formData);
                }
            }

            input.click();
        },

        async uploadFile(formData) {
            let response = await this.$store.dispatch("updateUserPhoto", formData);
            if (response.success) {
                this.user = Object.assign({}, this.$store.getters.currentUser);
                let photoResponse = await this.$store.dispatch("getByteArray", this.user.photoUrl);
                if (photoResponse.success) {
                    this.photoSrc = photoResponse.data;
                }
            }
        },
    }
}
</script>

<style scoped>
.logout_btn {
    color: #fff;
    text-decoration: none;
    font-weight: bold;
}

.search_field {
    color: #fff !important;
}

.profile_photo {
    border: 1px solid #a5a3a3;
}

.block_title {
    font-size: 22px;
    text-transform: uppercase;
}

.block_title_wrapper {
    border-bottom: 1px solid #000;
}

.block_main {
    width: 700px;
}

.block_password {
    width: 1000px;
}

.save_btn {
    width: 100%;
    color: white;
}

.header_link {
    color: #fff;
    text-decoration: none;
    font-weight: bold;
}

.header_link_wrapper {
    margin-left: 135px;
}
</style>
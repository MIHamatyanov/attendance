<template>
    <div class="main_left_pane">
        <v-app-bar
            fixed
            color="#003b73"
            height="100px"
            elevation="0"
        >
            <v-row no-gutters :class="$vuetify.breakpoint.smAndDown ? '' : 'px-10'">
                <v-icon v-if="$vuetify.breakpoint.smAndDown" color="white" size="30" class="mr-5" @click="drawer = true">mdi-menu</v-icon>
                <LogoIcon/>
                <div v-if="user.role !== 'ROLE_TEACHER'" :class="$vuetify.breakpoint.smAndDown ? 'ml-5 mt-2' : 'header_link_wrapper mt-3'">
                    <div v-if="!$vuetify.breakpoint.xsOnly">
                        <a v-for="(course, index) in courses" class="mr-5 header_link" :key="index"
                           @click="loadSubjects(course)">
                            <span :class="{'link_active': course.active}">{{ course.number }} курс</span>
                        </a>
                    </div>
                    <v-menu
                        v-else
                        offset-y
                    >
                        <template v-slot:activator="{ attrs, on }">
                            <a
                                class="header_link"
                                v-bind="attrs"
                                v-on="on"
                            >
                                {{selectedCourse !== null ? selectedCourse + ' курс' : 'Курс'}} <v-icon color="white" size="15">mdi-arrow-down-thick</v-icon>
                            </a>
                        </template>

                        <v-list>
                            <v-list-item
                                v-for="(course, index) in courses"
                                :key="index"
                                link
                            >
                                <a class="mr-5 header_link_2"
                                   @click="loadSubjects(course)">
                                    <span :class="{'link_active': course.active}">{{ course.number }} курс</span>
                                </a>
                            </v-list-item>
                        </v-list>
                    </v-menu>
                </div>
                <v-spacer></v-spacer>
                <router-link :to="{name: 'StudentProfile', query: {course: $route.query.course}}">
                    <v-avatar
                        :size="$vuetify.breakpoint.smAndDown ? 40 : 45"
                    >
                        <AvatarIconWhite/>
                    </v-avatar>
                </router-link>
                <a href="/logout" class="ml-5 logout_btn" :class="$vuetify.breakpoint.smAndDown ? 'mt-2' : 'mt-3'">
                    <v-icon v-if="$vuetify.breakpoint.smAndDown" color="white">mdi-logout</v-icon>
                    <span v-else>Выход</span>
                </a>
            </v-row>
        </v-app-bar>

        <v-navigation-drawer
            v-model="drawer"
            :fixed="$vuetify.breakpoint.smAndDown"
            :temporary="$vuetify.breakpoint.smAndDown"
            color="#003b73"
            class="nav-drawer"
            :permanent="$vuetify.breakpoint.mdAndUp"
            :width="$vuetify.breakpoint.mdAndUp ? '350px' : '350px'"
            height="100vh"
        >
            <v-icon v-if="$vuetify.breakpoint.smAndDown" color="white" size="30" class="pl-5 pt-5" @click="drawer = false">mdi-close</v-icon>
            <div v-if="user.role !== 'ROLE_TEACHER'" class="mt-5 px-10">
                <div v-if="subjects.firstSemester && subjects.firstSemester.length !== 0" class="ml-5">
                    <span class="subject_link">1 семестр</span>
                    <ul>
                        <li class="subject_link mt-2" v-for="(subject, index) in subjects.firstSemester" :key="index">
                            <router-link
                                :to="{name: 'SubjectVisitList', params: {id: subject.id}, query: {course: $route.query.course}}"
                                class="subject_link">{{ subject.name }}
                            </router-link>
                        </li>
                    </ul>
                </div>
                <div v-if="subjects.secondSemester && subjects.secondSemester.length !== 0" class="ml-5"
                     :class="subjects.firstSemester && subjects.firstSemester.length !== 0 ? 'mt-10' : ''">
                    <span class="subject_link">2 семестр</span>
                    <ul>
                        <li class="subject_link mt-2" v-for="(subject, index) in subjects.secondSemester" :key="index">
                            <router-link
                                :to="{name: 'SubjectVisitList', params: {id: subject.id}, query: {course: $route.query.course}}"
                                class="subject_link">{{ subject.name }}
                            </router-link>
                        </li>
                    </ul>
                </div>
            </div>
            <div v-else-if="user.role === 'ROLE_TEACHER'" class="mt-9 px-10">
                <div class="ml-5 mt-10" v-for="(daySubjects, index) in subjects" :key="index">
                    <span class="subject_link">{{ getWeekDayName(daySubjects.weekDay) }}</span>
                    <ul>
                        <li class="subject_link mt-2" v-for="(subject, index) in daySubjects.subjects" :key="index">
                            <router-link
                                :to="{name: 'TeacherSubjectVisitList', params: {id: subject.id}, query: {course: $route.query.course}}"
                                class="subject_link">{{ subject.name }}
                            </router-link>
                        </li>
                    </ul>
                </div>
            </div>
        </v-navigation-drawer>
    </div>
</template>

<script>

import LogoIcon from "./icons/LogoIcon";
import AvatarIconWhite from "./icons/AvatarIconWhite";

export default {
    name: "NavigationMenu",
    components: {AvatarIconWhite, LogoIcon},
    data() {
        return {
            drawer: false,
            subjects: [],
            courses: [],
            selectedCourse: null,
            user: {
                group: {}
            }
        }
    },

    watch: {
        '$route.query.course': function () {
            this.loadSubjects({number: this.$route.query.course});
        }
    },

    async created() {
        await this.$store.dispatch('loadCurrentUserData');
        this.user = Object.assign({}, this.$store.getters.currentUser);
        if (this.user.role !== 'ROLE_TEACHER') {
            for (let i = 0; i < this.user.group.course; i++) {
                this.courses.push({
                    number: i + 1,
                    active: false
                });
            }
            if (this.$route.query.course) {
                this.loadSubjects({number: this.$route.query.course});
            }
        }

        if (this.user.role === 'ROLE_TEACHER') {
            this.loadSubjects(0);
        }
    },
    methods: {
        async loadSubjects(course) {
            if (this.user.role !== 'ROLE_TEACHER') {
                this.$route.params.course = course.number;
                this.selectedCourse = course.number;
                this.$router.replace({name: this.$route.name, query: {course: course.number}}).catch(() => {
                });

                for (let i = 0; i < this.courses.length; i++) {
                    this.courses[i].active = false;
                }
                this.courses[course.number - 1].active = true;
            }

            let response;
            if (this.user.role !== 'ROLE_TEACHER') {
                response = await this.$store.dispatch('getSubjects', course.number);
            } else {
                response = await this.$store.dispatch('getTeacherSubjects');
            }

            if (response.success) {
                this.subjects = response.data;
            }
        },

        getWeekDayName(dayOfWeek) {
            switch (dayOfWeek) {
                case 1:
                    return 'Понедельник';
                case 2:
                    return 'Вторник';
                case 3:
                    return 'Среда';
                case 4:
                    return 'Четверг';
                case 5:
                    return 'Пятница';
                case 6:
                    return 'Суббота';
                case 7:
                    return 'Воскресенье';
            }
        }
    }
}
</script>

<style scoped>
.main_left_pane {
    min-height: 100vh;
    z-index: 1000;
}

@media screen and (min-width: 960px) {
    .main_left_pane {
        position: fixed;
        max-width: inherit;
    }
}

.nav-drawer {
    z-index: 1000;
}

.subject_link {
    color: white;
    text-decoration: none;
    font-weight: bold;
}


.header_link {
    color: #fff;
    text-decoration: none;
    font-weight: bold;
}

.header_link_2 {
    text-decoration: none;
    font-weight: bold;
    color: #000000;
}

.header_link_wrapper {
    margin-left: 135px;
}

.logout_btn {
    color: #fff;
    text-decoration: none;
    font-weight: bold;
}

.link_active {
    opacity: 0.7;
}
</style>
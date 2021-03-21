<template>
    <div class="main_left_pane">
        <v-navigation-drawer
            v-model="drawer"
            :absolute="$vuetify.breakpoint.smAndDown"
            :temporary="$vuetify.breakpoint.smAndDown"
            color="#003b73"
            class="nav-drawer"
            :permanent="$vuetify.breakpoint.mdAndUp"
            :width="$vuetify.breakpoint.mdAndUp ? '350px' : '350px'"
            height="100vh"
        >
            <div v-if="subjects.firstSemester && subjects.firstSemester.length !== 0" class="ml-5">
                <span class="subject_link">1 семестр</span>
                <ul>
                    <li class="subject_link mt-2" v-for="(subject, index) in subjects.firstSemester" :key="index">
                        <a class="subject_link">{{subject.name}}</a>
                    </li>
                </ul>
            </div>
            <div v-if="subjects.secondSemester && subjects.secondSemester.length !== 0" class="ml-5" :class="subjects.firstSemester && subjects.firstSemester.length !== 0 ? 'mt-10' : ''">
                <span class="subject_link">2 семестр</span>
                <ul>
                    <li class="subject_link mt-2" v-for="(subject, index) in subjects.secondSemester" :key="index">
                        <a class="subject_link">{{subject.name}}</a>
                    </li>
                </ul>
            </div>
        </v-navigation-drawer>
        <v-sheet v-if="!drawer" @click="drawer = !drawer" color="grey lighten-3" width="30px" height="100vh"
                 class="d-flex justify-space-around" style="position: absolute">
            <v-icon>mdi-arrow-collapse-right</v-icon>
        </v-sheet>
    </div>
</template>

<script>

export default {
    name: "NavigationMenu",
    data() {
        return {
            drawer: false,
            subjects: []
        }
    },

    watch: {
        '$route.query.course': function () {
            this.loadSubjects(this.$route.query.course);
        }
    },

    created() {
        if (this.$route.query.course) {
            this.loadSubjects(this.$route.query.course);
        }
    },
    methods: {
        async loadSubjects(course) {
            let response = await this.$store.dispatch('getSubjects', course);
            if (response.success) {
                this.subjects = response.data;
            }
        }
    }
}
</script>

<style scoped>
.main_left_pane {
    min-height: 100vh;
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
</style>
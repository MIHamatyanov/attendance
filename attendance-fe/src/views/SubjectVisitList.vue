<template>
    <div class="main_container">
        <v-row no-gutters style="margin-top: 100px;">
            <v-col v-if="$vuetify.breakpoint.mdAndUp" cols="1" md="3" lg="2">
                <NavigationMenu/>
            </v-col>
            <v-col cols="10" class="pl-12 mt-9">
                <span class="page_title">{{ getSubjectTitle() }}</span>
                <v-simple-table class="mt-5">
                    <template v-slot:default>
                        <colgroup>
                            <col span="1" style="width: 15%;">
                            <col :span="10" style="width: 5%;">
                            <col span="1" style="width: 70%;">
                            <col span="1" style="width: 5%;">
                        </colgroup>
                        <thead>
                        <tr>
                            <th class="text-left">
                                <span class="table_header">Фамилия И.О.</span>
                            </th>
                            <th class="text-left"  v-for="i in 10" :key="i">
                                <span class="table_header">29.02</span>
                            </th>
                            <th class="text-left">
                                <span class="table_header">...</span>
                            </th>
                            <th class="text-left">
                                <span class="table_header">Баллы</span>
                            </th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr
                            v-for="(visitList, index) in userVisitList"
                            :key="index"
                            style="cursor: pointer"
                        >
                            <td class="num">
                                {{ visitList.user.surname + ' ' + visitList.user.name.substr(0, 1) + '. ' + visitList.user.patronymic.substr(0, 1) + '.' }}
                            </td>
                            <td>
                                {{ '' }}
                            </td>
                            <td v-for="i in 10" :key="i">
                                {{ '' }}
                            </td>
                            <td>
                                {{ '' }}
                            </td>
                        </tr>
                        </tbody>
                    </template>
                </v-simple-table>
            </v-col>
        </v-row>
    </div>
</template>

<script>
import NavigationMenu from "../components/NavigationMenu";
export default {
    name: "SubjectVisitList",
    components: {NavigationMenu},
    data() {
        return {
            subject: {},
            userVisitList: []
        }
    },

    watch: {
        '$route.params.id': function () {
            this.getSubjectVisitList();
        }
    },

    created() {
        this.getSubjectVisitList();
    },

    methods: {
        async getSubjectVisitList() {
            let response = await this.$store.dispatch('getSubjectVisitList', this.$route.params.id);
            if (response.success) {
                this.subject = response.data.subject;
                this.userVisitList = response.data.userVisitList;
            }
        },

        getSubjectTitle() {
            if (this.subject.teacher) {
                let teacher = this.subject.teacher;
                return this.subject.name + ' (' + teacher.surname + ' ' + teacher.name.substr(0, 1) + '. ' + teacher.patronymic.substr(0, 1) + '.' + ')';
            }

            return '';
        }
    }
}
</script>

<style scoped>
.page_title {
    font-weight: bold;
    font-size: 20px;
}

th, td {
    border: 1px solid black !important;
}
</style>
<template>
    <div class="main_container">
        <v-row no-gutters style="margin-top: 100px;">
            <v-col md="3" lg="2">
                <NavigationMenu/>
            </v-col>
            <v-col cols="12" md="9" lg="10" class="px-5 mt-9" :class="{'pl-12' : $vuetify.breakpoint.smAndUp}">
                <v-row no-gutters>
                    <span class="page_title">{{ getSubjectTitle() }}</span>
                    <span class="page_title" :class="$vuetify.breakpoint.xsOnly ? '' : 'ml-2'">{{ getScheduleStr() }}</span>
                </v-row>
                <v-row no-gutters>
                    <v-col cols="12">
                        <span class="subgroup_title">1 подгруппа</span>
                        <v-simple-table class="mt-5">
                            <template v-slot:default>
                                <colgroup>
                                    <col span="1">
                                    <col v-if="subGroups[0].userVisitList[0].user && subGroups[0].userVisitList[0].visitList.length > 0"
                                         :span="subGroups[0].userVisitList[0].visitList.length" style="width: 20px;">
                                    <col span="1" style="width: 20px;">
                                    <col span="1" style="width: 1000px;">
                                    <col span="2" style="width: 20px;">
                                </colgroup>
                                <thead>
                                <tr>
                                    <th class="text-center" style="min-width: 250px;">
                                        <span class="table_header">Фамилия И.О.</span>
                                    </th>
                                    <th class="text-center" v-for="(visitList, index) in subGroups[0].userVisitList[0].visitList"
                                        :key="index">
                                        <span class="table_header">{{ parseDate(visitList.date) }}</span>
                                    </th>
                                    <th class="text-center">
                                        <v-dialog
                                            ref="dialog1"
                                            v-model="datepickerMenu1"
                                            :return-value.sync="firstSubGroupDate"
                                            persistent
                                            width="290px"
                                        >
                                            <template v-slot:activator="{ on, attrs }">
                                                <v-text-field
                                                    v-show="firstSubGroupFormattedDate === ''"
                                                    v-model="firstSubGroupFormattedDate"
                                                    style="width: 30px;"
                                                    readonly
                                                    v-bind="attrs"
                                                    v-on="on"
                                                ></v-text-field>
                                                <span class="table_header text-decoration-underline" v-show="firstSubGroupFormattedDate !== ''" v-bind="attrs"
                                                      v-on="on">
                                                    {{firstSubGroupFormattedDate}}
                                                </span>
                                            </template>
                                            <v-date-picker
                                                v-model="firstSubGroupDate"
                                                first-day-of-week="1"
                                                locale="ru"
                                                @input="firstSubGroupFormattedDate = parseDate(firstSubGroupDate)"
                                                scrollable
                                            >
                                                <v-spacer></v-spacer>
                                                <v-btn
                                                    text
                                                    color="primary"
                                                    @click="datepickerMenu1 = false"
                                                >
                                                    Отмена
                                                </v-btn>
                                                <v-btn
                                                    text
                                                    color="primary"
                                                    @click="$refs.dialog1.save(firstSubGroupDate)"
                                                >
                                                    OK
                                                </v-btn>
                                            </v-date-picker>
                                        </v-dialog>
                                    </th>
                                    <th class="text-center">
                                        <span class="table_header">...</span>
                                    </th>
                                    <th class="text-center">
                                        <span class="table_header">Н</span>
                                    </th>
                                    <th class="text-center">
                                        <span class="table_header">Баллы</span>
                                    </th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr
                                    v-for="(visitList, index) in subGroups[0].userVisitList"
                                    :key="index"
                                    style="cursor: pointer"
                                >
                                    <td v-if="visitList.user">
                                        {{
                                            visitList.user.surname + ' ' + visitList.user.name.substr(0, 1) + '. ' + visitList.user.patronymic.substr(0, 1) + '.'
                                        }}
                                    </td>
                                    <td class="text-center" v-for="(visitList, index) in subGroups[0].userVisitList[index].visitList" :key="index">
                                        <v-text-field
                                            v-if="user.role !== 'ROLE_STUDENT'"
                                            v-model="visitList.mark"
                                        ></v-text-field>
                                        <span v-else>{{ visitList.mark }}</span>
                                    </td>
                                    <td class="text-center" v-if="user.role !== 'ROLE_STUDENT'">
                                        <v-text-field v-model="visitList.mark">
                                        </v-text-field>
                                    </td>
                                    <td>
                                        {{ '' }}
                                    </td>
                                    <td class="text-center">
                                        {{ visitList.notVisitCount }}
                                    </td>
                                    <td class="text-center">
                                        {{ visitList.marksSum }}
                                    </td>
                                </tr>
                                </tbody>
                            </template>
                        </v-simple-table>
                    </v-col>

                    <v-col cols="12" class="mt-10">
                        <span class="subgroup_title">2 подгруппа</span>
                        <v-simple-table class="mt-5">
                            <template v-slot:default>
                                <colgroup>
                                    <col span="1">
                                    <col v-if="subGroups[1].userVisitList[0].user && subGroups[1].userVisitList[0].visitList.length > 0"
                                         :span="subGroups[1].userVisitList[0].visitList.length" style="width: 20px;">
                                    <col span="1" style="width: 20px;">
                                    <col span="1" style="width: 1000px;">
                                    <col span="2" style="width: 20px;">
                                </colgroup>
                                <thead>
                                <tr>
                                    <th class="text-center">
                                        <span class="table_header">Фамилия И.О.</span>
                                    </th>
                                    <th class="text-center" v-for="(visitList, index) in subGroups[1].userVisitList[0].visitList"
                                        :key="index">
                                        <span class="table_header">{{ parseDate(visitList.date) }}</span>
                                    </th>
                                    <th class="text-center">
                                        <v-dialog
                                            ref="dialog2"
                                            v-model="datepickerMenu2"
                                            :return-value.sync="firstSubGroupDate"
                                            persistent
                                            width="290px"
                                        >
                                            <template v-slot:activator="{ on, attrs }">
                                                <v-text-field
                                                    v-show="firstSubGroupFormattedDate === ''"
                                                    v-model="firstSubGroupFormattedDate"
                                                    style="width: 30px;"
                                                    readonly
                                                    v-bind="attrs"
                                                    v-on="on"
                                                ></v-text-field>
                                                <span class="table_header text-decoration-underline" v-show="firstSubGroupFormattedDate !== ''" v-bind="attrs"
                                                      v-on="on">
                                                    {{firstSubGroupFormattedDate}}
                                                </span>
                                            </template>
                                            <v-date-picker
                                                v-model="firstSubGroupDate"
                                                first-day-of-week="1"
                                                locale="ru"
                                                @input="firstSubGroupFormattedDate = parseDate(firstSubGroupDate)"
                                                scrollable
                                            >
                                                <v-spacer></v-spacer>
                                                <v-btn
                                                    text
                                                    color="primary"
                                                    @click="datepickerMenu2 = false"
                                                >
                                                    Отмена
                                                </v-btn>
                                                <v-btn
                                                    text
                                                    color="primary"
                                                    @click="$refs.dialog2.save(firstSubGroupDate)"
                                                >
                                                    OK
                                                </v-btn>
                                            </v-date-picker>
                                        </v-dialog>
                                    </th>
                                    <th class="text-center">
                                        <span class="table_header">...</span>
                                    </th>
                                    <th class="text-center">
                                        <span class="table_header">Н</span>
                                    </th>
                                    <th class="text-center">
                                        <span class="table_header">Баллы</span>
                                    </th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr
                                    v-for="(visitList, index) in subGroups[1].userVisitList"
                                    :key="index"
                                    style="cursor: pointer"
                                >
                                    <td v-if="visitList.user">
                                        {{
                                            visitList.user.surname + ' ' + visitList.user.name.substr(0, 1) + '. ' + visitList.user.patronymic.substr(0, 1) + '.'
                                        }}
                                    </td>
                                    <td class="text-center" v-for="(visitList, index) in subGroups[1].userVisitList[index].visitList" :key="index">
                                        <v-text-field
                                            v-if="user.role !== 'ROLE_STUDENT'"
                                            v-model="visitList.mark"
                                        ></v-text-field>
                                        <span v-else>{{ visitList.mark }}</span>
                                    </td>
                                    <td class="text-center" v-if="user.role !== 'ROLE_STUDENT'">
                                        <v-text-field v-model="visitList.mark">
                                        </v-text-field>
                                    </td>
                                    <td>
                                        {{ '' }}
                                    </td>
                                    <td class="text-center">
                                        {{ visitList.notVisitCount }}
                                    </td>
                                    <td class="text-center">
                                        {{ visitList.marksSum }}
                                    </td>
                                </tr>
                                </tbody>
                            </template>
                        </v-simple-table>
                    </v-col>
                </v-row>
                <v-row no-gutters class="mt-10">
                    <v-col cols="12" class="d-flex justify-end">
                        <v-btn color="#003b73" dark @click="save">Сохранить</v-btn>
                    </v-col>
                </v-row>
            </v-col>
        </v-row>
    </div>
</template>

<script>
import NavigationMenu from "../components/NavigationMenu";

export default {
    name: "TeacherSubjectVisitList",
    components: {NavigationMenu},
    data() {
        return {
            subject: {},
            subGroups: [],
            datepickerMenu1: false,
            datepickerMenu2: false,
            firstSubGroupDate: '',
            firstSubGroupFormattedDate: '',
            secondSubGroupDate: '',
            secondSubGroupFormattedDate: '',
            user: {}
        }
    },

    watch: {
        '$route.params.id': function () {
            this.getSubjectVisitList();
        }
    },

    created() {
        this.user = this.$store.getters.currentUser;
        this.getSubjectVisitList();
    },

    methods: {
        async getSubjectVisitList() {
            let response = await this.$store.dispatch('getTeacherSubjectVisitList', this.$route.params.id);
            if (response.success) {
                this.subject = response.data.subject;
                this.subGroups = response.data.subGroups;
            }
        },

        getSubjectTitle() {
            return this.subject.name + ' ' + this.subject.group.name;
        },

        getScheduleStr() {
            let schedule = this.subject.schedule;
            if (!schedule) {
                return;
            }
            let scheduleStr = '';
            for (let i = 0; i < schedule.length; i++) {
                scheduleStr += this.getWeekDayName(schedule[i].weekDay) + ' ';

                if (schedule[i].evenWeek !== null) {
                    scheduleStr += schedule[i].evenWeek ? 'ч/н ' : 'н/н ';
                }

                scheduleStr += schedule[i].startTime.substring(0, schedule[i].startTime.lastIndexOf(":")) + ' - ' + schedule[i].endTime.substring(0, schedule[i].endTime.lastIndexOf(":"));

                if (i + 1 < schedule.length) {
                    scheduleStr += '; ';
                }
            }

            return scheduleStr;
        },

        getWeekDayName(dayOfWeek) {
            switch (dayOfWeek) {
                case 1:
                    return 'ПН';
                case 2:
                    return 'ВТ';
                case 3:
                    return 'СР';
                case 4:
                    return 'ЧТ';
                case 5:
                    return 'ПТ';
                case 6:
                    return 'СБ';
                case 7:
                    return 'ВС';
            }
        },

        parseDate(date) {
            let d = new Date(date);
            let day = d.getDate();
            if ((day + '').length === 1) {
                day = '0' + day;
            }

            let month = d.getMonth();
            if ((month + '').length === 1) {
                month = '0' + month;
            }
            return day + '.' + month;
        },

        async save() {
            let date = this.firstSubGroupDate === '' ? null : this.firstSubGroupDate;
            this.subGroups[0].date = date;
            this.subGroups[1].date = date;
            let data = {
                subject: this.subject,
                subGroups: this.subGroups
            };

            let response = await this.$store.dispatch("saveTeacherSubjectVisitList", data);
            if (response.success) {
                this.firstSubGroupDate = '';
                this.secondSubGroupDate = '';
                this.firstSubGroupFormattedDate = '';
                this.secondSubGroupFormattedDate = '';
                this.getSubjectVisitList();
            }
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

.subgroup_title {
    font-weight: bold;
    font-size: 18px;
}
</style>
<template>
    <div class="main_container">
        <v-row no-gutters style="margin-top: 100px;">
            <v-col md="3" lg="2">
                <NavigationMenu/>
            </v-col>
            <v-col cols="12" md="9" lg="10" class="mt-9" :class="{'pl-12' : $vuetify.breakpoint.smAndUp}">
                <v-row no-gutters class="px-5">
                    <v-col cols="12">
                        <v-row no-gutters>Выгрузка отчета</v-row>
                        <v-row no-gutters class="mt-7">
                            <v-col cols="12" md="3" lg="1">
                                <div class="field-label">Курс</div>
                            </v-col>
                            <v-col cols="12" sm="3">
                                <v-select
                                    v-model="course"
                                    :items="['1','2','3','4']"
                                    outlined
                                ></v-select>
                            </v-col>
                        </v-row>
                        <v-row no-gutters class="mt-5">
                            <v-col cols="12" md="3" lg="1">
                                <div class="field-label">Группы</div>
                            </v-col>
                            <v-col cols="12" sm="3">
                                <v-select
                                    v-model="selectedGroupIds"
                                    :items="groups"
                                    item-text="label"
                                    item-value="value"
                                    multiple
                                    outlined
                                ></v-select>
                            </v-col>
                        </v-row>
                        <v-row no-gutters class="mt-7">
                            <v-col cols="12" md="3" lg="1">
                                <v-btn @click="download">Выгрузить</v-btn>
                            </v-col>
                        </v-row>
                    </v-col>
                </v-row>
            </v-col>
        </v-row>
    </div>
</template>

<script>
import NavigationMenu from "../components/NavigationMenu";

export default {
    name: "DeanMain",
    components: {NavigationMenu},
    data() {
        return {
            course: '1',
            groups: [],
            selectedGroupIds: []
        }
    },

    created() {
        this.loadGroups();
    },

    watch: {
        course() {
            this.selectedGroupIds = [];
            this.loadGroups();
        }
    },

    methods: {
        async loadGroups() {
            let response = await this.$store.dispatch("getGroups", this.course);

            if (response.success) {
                let newGroups = [];
                response.data.forEach(group => {
                    let newGroup = {
                        value: group.id,
                        label: group.name
                    };
                    newGroups.push(newGroup)
                });
                this.groups = newGroups;
            }
        },
        async download() {
            let data = {
                course: this.course,
                groupIds: this.selectedGroupIds
            };

            let response = await this.$store.dispatch('generateReport', data);
            const blob = new Blob([response.data], {type: 'application/pdf'});
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = 'report';
            link.target = "_blank";
            link.click();
            link.remove();
        }
    }
}
</script>

<style scoped>
.field-label {
    font-size: 16px;
    padding: 16px 0 0 0;
}
</style>
import rest from "../../restUtils";

const path = '/subject';

export default {
    namespace: true,
    state: {},

    actions: {
        async getSubjects(context, course) {
            try {
                let subjects = await rest.doGet(`${path}?course=${course}`);

                return {
                    success: true,
                    data: subjects
                };
            } catch (error) {
                return {
                    success: false,
                    data: error.response.data
                };
            }
        },

        async getSubjectVisitList(context, id) {
            try {
                let visitList = await rest.doGet(`${path}/${id}/visit-list`);

                return {
                    success: true,
                    data: visitList
                };
            } catch (error) {
                return {
                    success: false,
                    data: error.response.data
                };
            }
        },

        async saveSubjectVisitList(context, data) {
            try {
                let visitList = await rest.doPost(`${path}/${data.subject.id}/visit-list`, data);

                return {
                    success: true,
                    data: visitList
                };
            } catch (error) {
                return {
                    success: false,
                    data: error.response.data
                };
            }
        },
    },

    mutations: {},

    getters: {}
}
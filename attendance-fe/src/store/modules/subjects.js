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
    },

    mutations: {},

    getters: {}
}
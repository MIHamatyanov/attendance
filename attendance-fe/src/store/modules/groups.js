import rest from "../../restUtils";

const path = '/group';

export default {
    namespace: true,
    state: {},

    actions: {
        async getGroups(context, course) {
            try {
                let groups = await rest.doGet(`${path}?course=${course}`);

                return {
                    success: true,
                    data: groups
                };
            } catch (error) {
                return {
                    success: false,
                    data: error.response.data
                };
            }
        }
    },

    mutations: {},

    getters: {}
}
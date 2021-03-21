import rest from "../../restUtils";

const path = '/files';

export default {
    namespace: true,
    state: {},

    actions: {
        async getByteArray(context, filename) {
            try {
                let response = await rest.getFile(
                    `${path}/` + filename);

                return {
                    success: true,
                    data: 'data:image/jpeg;base64,' + new Buffer(response, 'binary').toString('base64')
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
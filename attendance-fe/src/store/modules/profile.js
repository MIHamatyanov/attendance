import constants from '@/constants';
import rest from "../../restUtils";
import router from '@/router';


const emptyUser = {
    id: 0,
    name: '',
    surname: '',
    patronymic: '',
    email: '',
    role: ''
};

export default {
    namespace: true,
    state: {
        user: Object.assign({}, emptyUser),
        token: '',
        role: ''
    },

    actions: {
        async authorization({commit, dispatch}, user) {
            try {
                let tokenData = await rest.doPost(
                    `/auth/login`,
                    {
                        email: user.email,
                        password: user.password
                    });
                commit('AUTH_SUCCESS', tokenData);
            } catch (error) {
                commit('AUTH_ERROR');
                return error;
            }
            try {
                await dispatch('loadCurrentUserData');
                dispatch('redirectAfterLogin');
            } catch (error) {
                dispatch('logout');
                router.push({name: 'Index'});
            }
        },

        async isAuthenticated({dispatch}) {
            let authenticated = false;
            let tokenFromStorage = localStorage.getItem(constants.SESSION_STORAGE_TOKEN);
            if (tokenFromStorage) {
                authenticated = await dispatch('loadCurrentUserData');
            }
            return !!authenticated;
        },


        async loadCurrentUserData({commit, dispatch}) {
            let success;
            try {
                let userData = await rest.doGet(`/user`);
                commit('LOAD_USER_DATA', userData);

                success = true;
            } catch (error) {
                success = false;
                dispatch('logout');
            }

            return success;
        },

        async updateUserData({commit}, user) {
            try {
                let userData = await rest.doPost(`/user`, user);
                commit('LOAD_USER_DATA', userData);

                return {
                    success: true,
                    data: userData
                };
            } catch (error) {
                return {
                    success: false,
                    data: error.response.data
                }
            }
        },

        async updateUserPhoto({commit}, formData) {
            try {
                let userData = await rest.doPost(
                    `/user/photo`, formData);
                commit('LOAD_USER_DATA', userData);

                return {
                    success: true,
                    data: userData
                };
            } catch (error) {
                return {
                    success: false,
                    data: error.response.data
                };
            }
        },

        async deleteUserPhoto({commit}) {
            try {
                let userData = await rest.doDelete(
                    `/user/photo`);
                commit('LOAD_USER_DATA', userData);

                return {
                    success: true,
                    data: userData
                };
            } catch (error) {
                return {
                    success: false,
                    data: error.response.data
                };
            }
        },

        async changePassword(context, data) {
            try {
                await rest.doPost('/user/change-password', data);
                return {
                    success: true
                }
            } catch (error) {
                return {
                    success: false,
                    data: error
                }
            }
        },

        async logout({commit}) {
            commit('LOGOUT');
        },

        redirectAfterLogin() {
            let redirectTo = sessionStorage.getItem(constants.SESSION_STORAGE_REDIRECT);
            if (redirectTo && redirectTo.length > 0) {
                sessionStorage.removeItem(constants.SESSION_STORAGE_REDIRECT);
                router.push(redirectTo);
            } else {
                router.push({name: 'StudentProfile'});
            }
        }
    },

    mutations: {
        AUTH_SUCCESS(state, user) {
            state.token = user.token;
            state.role = user.role;
            localStorage.setItem(constants.SESSION_STORAGE_TOKEN, user.token);
        },

        AUTH_ERROR(state) {
            state.token = '';
            state.user = Object.assign({}, emptyUser);
        },

        LOGOUT(state) {
            state.token = '';
            state.user = Object.assign({}, emptyUser);
            localStorage.removeItem(constants.SESSION_STORAGE_TOKEN);
        },

        LOAD_USER_DATA(state, payload) {
            Object.assign(state.user, payload);
        }
    },

    getters: {
        currentUser: state => state.user,
        token: state => state.token
    }
};
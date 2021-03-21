import Vue from 'vue'
import Vuex from 'vuex'
import profile from './store/modules/profile.js';
import users from './store/modules/users.js';
import files from './store/modules/files.js';
import subjects from './store/modules/subjects.js';

import createPersistedState from 'vuex-persistedstate'
import * as Cookies from 'js-cookie'

Vue.use(Vuex);

export default new Vuex.Store({
    strict: true,
    modules: {
        profile,
        users,
        files,
        subjects
    },

    state: {
    },

    actions: {
    },

    mutations: {
    },

    getters: {
    },

    plugins: [createPersistedState({
        key: 'profile',
        paths: ['profile'],
        storage: {
            getItem: key => Cookies.get(key),
            setItem: (key, value) =>
                Cookies.set(key, value, {expires: 3}),
            removeItem: key => Cookies.remove(key)
        }
    })]
})

import Vue from 'vue'
import Router from 'vue-router'
import store from './store';
import constants from './constants';

// eslint-disable-next-line no-unused-vars
import Login from './views/Login';
import Profile from './views/Profile';
import Logout from './views/Logout';

Vue.use(Router);

const checkAuthAndRoles = async (to, from, next, roles) => {
    let auth = await store.dispatch('isAuthenticated');

    if (!auth) {
        localStorage.setItem(constants.SESSION_STORAGE_REDIRECT, to.path);
        next({name: 'Login'});
        return;
    }

    if (roles && roles.length > 0) {
        const roleCode = store.getters.currentUser.role;
        let access = roles.indexOf(roleCode) !== -1;

        if (!access) {
            if (from.params.code === '403')
                next(false);
            else
                next({name: 'Login'});
            return;
        }
    }

    next();
};

const checkNoAuth = async (next) => {
    let auth = await store.dispatch('isAuthenticated');
    if (auth) {
        next({name: 'Profile'});
        return;
    }
    next();
};


export default new Router({
    mode: 'history',
    base: process.env.BASE_URL,
    routes: [

        {
            path: '/',
            name: 'Login',
            component: Login,
            beforeEnter: (to, from, next) => {
                checkNoAuth(next);
            }
        },

        {
            path: '/profile',
            name: 'Profile',
            component: Profile,
            beforeEnter: (to, from, next) => {
                checkAuthAndRoles(to, from, next, [constants.ROLE.GROUP_HEAD])
            }
        },

        {
            path: '/logout',
            name: 'Logout',
            component: Logout
        },

        {
            path: '/*',
            redirect: '/404'
        }
    ]
})



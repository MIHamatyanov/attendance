import Vue from 'vue'
import Router from 'vue-router'
import store from './store';
import constants from './constants';

// eslint-disable-next-line no-unused-vars
import Login from './views/Login';
import StudentProfile from './views/StudentProfile';
import Logout from './views/Logout';
import SubjectVisitList from "./views/SubjectVisitList";

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
        next({name: 'StudentProfile'});
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
            name: 'StudentProfile',
            component: StudentProfile,
            beforeEnter: (to, from, next) => {
                checkAuthAndRoles(to, from, next, [constants.ROLE.GROUP_HEAD, constants.ROLE.STUDENT])
            }
        },

        {
            path: '/subject-visit-list/:id',
            name: 'SubjectVisitList',
            props: true,
            component: SubjectVisitList,
            beforeEnter: (to, from, next) => {
                checkAuthAndRoles(to, from, next, [constants.ROLE.GROUP_HEAD, constants.ROLE.STUDENT])
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



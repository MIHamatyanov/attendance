import axios from 'axios';
import store from '@/store';

const apiUrl = process.env.VUE_APP_GLOBAL_URL;

const headers = () => {
    let headers = {
        headers: {
            'Content-Type': 'application/json'
        }
    };

    let token = store.getters.token;
    if (token)
        Object.assign(headers.headers, {Authorization: token});

    return headers;
};

const multipartHeaders = () => {
    let headers = {
        headers: {
        }
    };

    let token = store.getters.token;
    if (token)
        Object.assign(headers.headers, {Authorization: token});

    return headers;
};

const restPromise = (axiosPromise) => {
    return new Promise((resolve, reject) => {
        axiosPromise
            .then(response => {
                resolve(response.data);
            })
            .catch((error) => {
                reject(error);
            })
    })
};

export default {
    getFile(url) {
        return restPromise(
            axios.get(apiUrl + url, { headers : {Authorization: store.getters.token}, responseType: 'arraybuffer'})
        );
    },

    doGet(url) {
        return restPromise(
            axios.get(apiUrl + url, headers())
        );
    },

    doPut(url, body) {
        return restPromise(
            axios.put(apiUrl + url, body ? body : {}, headers())
        );
    },

    doPost(url, body) {
        return restPromise(
            axios.post(apiUrl + url, body ? body : {}, headers())
        );
    },

    doPostMultipart(url, body) {
        return restPromise(
            axios.post(apiUrl + url, body ? body : {}, multipartHeaders())
        );
    },

    doDelete(url) {
        return restPromise(
            axios.delete(apiUrl + url, headers())
        );
    }
}

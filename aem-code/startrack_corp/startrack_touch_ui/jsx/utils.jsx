'use strict';

export default {
    ajaxGet(url, s, f) {
        var r = new XMLHttpRequest();
        r.open("GET", url, true);
        r.onreadystatechange = function () {
            if (r.readyState !== 4 || r.status !== 200) {
                return;
            }
            var data = JSON.parse(r.responseText);
            s(data);
        };
        r.onerror = function (error) {
            if (f) {
                f(error);
            }
        };
        r.onload = function() {
            if (r.status === 404 && f) {
                f(r.status);
            }
        }
        r.setRequestHeader('Accept', 'application/json');
        r.setRequestHeader('Content-type', 'application/json');
        r.send();
    },

    ajaxForm(url, data, s, f) {
        var r = new XMLHttpRequest();
        r.open("POST", url, true);
        r.onreadystatechange = function () {
            if (r.readyState !== 4 || r.status !== 200) {
                return;
            }
            var data = JSON.parse(r.responseText);
            s(data);
        };
        r.onerror = function (error) {
            if (f) {
                f(error);
            }
        };
        r.send(data);
    },

    ajaxPost: function(url, data, s, f) {
        var r = new XMLHttpRequest();
        r.open("POST", url, true);
        r.onreadystatechange = function () {
            if (r.readyState !== 4 || r.status !== 200) {
                return;
            }
            var data = JSON.parse(r.responseText);
            s(data);
        };
        r.onerror = function (error) {
            if (f) {
                f(error);
            }
        };
        r.setRequestHeader('Accept', 'application/json');
        r.setRequestHeader('Content-type', 'application/json');
        r.send(JSON.stringify(data));
    },

    addClass(el, className) {
        if (el.classList) {
            el.classList.add(className);
        } else {
            el.className += ' ' + className;
        }
    },

    toggleClass(el, className) {
        if (el.classList) {
            el.classList.toggle(className);
        } else {
            var classes = el.className.split(' ');
            var existingIndex = classes.indexOf(className);
            if (existingIndex >= 0) {
                classes.splice(existingIndex, 1);
            } else {
                classes.push(className);
            }
            el.className = classes.join(' ');
        }
    }
};

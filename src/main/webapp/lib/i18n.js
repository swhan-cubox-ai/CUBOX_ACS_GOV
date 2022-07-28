!function(e) {
    Array.prototype.indexOf || (Array.prototype.indexOf = function(e) {
        "use strict";
        if (null == this)
            throw new TypeError;
        var n = Object(this)
          , t = n.length >>> 0;
        if (0 == t)
            return -1;
        var r = 0;
        if (0 < arguments.length && ((r = Number(arguments[1])) != r ? r = 0 : 0 != r && r != 1 / 0 && r != -1 / 0 && (r = (0 < r || -1) * Math.floor(Math.abs(r)))),
        t <= r)
            return -1;
        for (var a = 0 <= r ? r : Math.max(t - Math.abs(r), 0); a < t; a++)
            if (a in n && n[a] === e)
                return a;
        return -1
    }
    ),
    Array.prototype.lastIndexOf || (Array.prototype.lastIndexOf = function(e) {
        "use strict";
        if (null == this)
            throw new TypeError;
        var n = Object(this)
          , t = n.length >>> 0;
        if (0 == t)
            return -1;
        var r = t;
        1 < arguments.length && ((r = Number(arguments[1])) != r ? r = 0 : 0 != r && r != 1 / 0 && r != -1 / 0 && (r = (0 < r || -1) * Math.floor(Math.abs(r))));
        for (var a = 0 <= r ? Math.min(r, t - 1) : t - Math.abs(r); 0 <= a; a--)
            if (a in n && n[a] === e)
                return a;
        return -1
    }
    ),
    "function" != typeof String.prototype.trim && (String.prototype.trim = function() {
        return this.replace(/^\s+|\s+$/g, "")
    }
    );
    var f, c = e.jQuery || e.Zepto, N = {}, j = {}, p = 0, P = [], d = !1, g = {}, n = null;
    "undefined" != typeof module && module.exports ? module.exports = N : (c && (c.i18n = c.i18n || N),
    e.i18n && (n = e.i18n),
    e.i18n = N);
    var T = {
        lng: void 0,
        load: "all",
        preload: [],
        lowerCaseLng: !(g = {
            load: function(o, i, s) {
                i.useLocalStorage ? g._loadLocal(o, i, function(e, t) {
                    for (var n = [], r = 0, a = o.length; r < a; r++)
                        t[o[r]] || n.push(o[r]);
                    0 < n.length ? g._fetch(n, i, function(e, n) {
                        C.extend(t, n),
                        g._storeLocal(n),
                        s(e, t)
                    }) : s(e, t)
                }) : g._fetch(o, i, function(e, n) {
                    s(e, n)
                })
            },
            _loadLocal: function(e, r, a) {
                var o = {}
                  , i = (new Date).getTime();
                if (window.localStorage) {
                    var s = e.length;
                    C.each(e, function(e, n) {
                        var t = C.localStorage.getItem("res_" + n);
                        t && (t = JSON.parse(t)).i18nStamp && t.i18nStamp + r.localStorageExpirationTime > i && (o[n] = t),
                        0 === --s && a(null, o)
                    })
                }
            },
            _storeLocal: function(e) {
                if (window.localStorage)
                    for (var n in e)
                        e[n].i18nStamp = (new Date).getTime(),
                        C.localStorage.setItem("res_" + n, JSON.stringify(e[n]))
            },
            _fetch: function(n, a, o) {
                var e = a.ns
                  , i = {};
                if (a.dynamicLoad) {
                    function r(e, n) {
                        o(e, n)
                    }
                    if ("function" == typeof a.customLoad)
                        a.customLoad(n, e.namespaces, a, r);
                    else {
                        var s = _(a.resGetPath, {
                            lng: n.join("+"),
                            ns: e.namespaces.join("+")
                        });
                        C.ajax({
                            url: s,
                            cache: a.cache,
                            success: function(e, n, t) {
                                C.log("loaded: " + s),
                                r(null, e)
                            },
                            error: function(e, n, t) {
                                C.log("failed loading: " + s),
                                r("failed loading resource.json error: " + t)
                            },
                            dataType: "json",
                            async: a.getAsync,
                            timeout: a.ajaxTimeout
                        })
                    }
                } else {
                    var l, u = e.namespaces.length * n.length;
                    C.each(e.namespaces, function(e, r) {
                        C.each(n, function(e, t) {
                            function n(e, n) {
                                e && (l = l || []).push(e),
                                i[t] = i[t] || {},
                                i[t][r] = n,
                                0 === --u && o(l, i)
                            }
                            "function" == typeof a.customLoad ? a.customLoad(t, r, a, n) : g._fetchOne(t, r, a, n)
                        })
                    })
                }
            },
            _fetchOne: function(e, n, t, a) {
                var o = _(t.resGetPath, {
                    lng: e,
                    ns: n
                });
                C.ajax({
                    url: o,
                    cache: t.cache,
                    success: function(e, n, t) {
                        C.log("loaded: " + o),
                        a(null, e)
                    },
                    error: function(e, n, t) {
                        if (n && 200 == n || e && e.status && 200 == e.status)
                            C.error("There is a typo in: " + o);
                        else if (n && 404 == n || e && e.status && 404 == e.status)
                            C.log("Does not exist: " + o);
                        else {
                            var r = n || (e && e.status ? e.status : null);
                            C.log(r + " when loading " + o)
                        }
                        a(t, {})
                    },
                    dataType: "json",
                    async: t.getAsync,
                    timeout: t.ajaxTimeout,
                    headers: t.headers
                })
            },
            postMissing: function(e, i, s, l, n) {
                var t = {};
                t[s] = l;
                var r = [];
                if ("fallback" === T.sendMissingTo && !1 !== T.fallbackLng[0])
                    for (var a = 0; a < T.fallbackLng.length; a++)
                        r.push({
                            lng: T.fallbackLng[a],
                            url: _(T.resPostPath, {
                                lng: T.fallbackLng[a],
                                ns: i
                            })
                        });
                else if ("current" === T.sendMissingTo || "fallback" === T.sendMissingTo && !1 === T.fallbackLng[0])
                    r.push({
                        lng: e,
                        url: _(T.resPostPath, {
                            lng: e,
                            ns: i
                        })
                    });
                else if ("all" === T.sendMissingTo) {
                    a = 0;
                    for (var o = n.length; a < o; a++)
                        r.push({
                            lng: n[a],
                            url: _(T.resPostPath, {
                                lng: n[a],
                                ns: i
                            })
                        })
                }
                for (var u = 0, f = r.length; u < f; u++) {
                    var c = r[u];
                    C.ajax({
                        url: c.url,
                        type: T.sendType,
                        data: t,
                        success: function(e, n, t) {
                            C.log("posted missing key '" + s + "' to: " + c.url);
                            for (var r = s.split("."), a = 0, o = j[c.lng][i]; r[a]; )
                                o = a === r.length - 1 ? o[r[a]] = l : o[r[a]] = o[r[a]] || {},
                                a++
                        },
                        error: function(e, n, t) {
                            C.log("failed posting missing key '" + s + "' to: " + c.url)
                        },
                        dataType: "json",
                        async: T.postAsync,
                        timeout: T.ajaxTimeout
                    })
                }
            },
            reload: function(e) {
                j = {},
                s(f, e)
            }
        }),
        returnObjectTrees: !1,
        fallbackLng: ["dev"],
        fallbackNS: [],
        detectLngQS: "setLng",
        detectLngFromLocalStorage: !1,
        ns: {
            namespaces: ["translation"],
            defaultNs: "translation"
        },
        fallbackOnNull: !0,
        fallbackOnEmpty: !1,
        fallbackToDefaultNS: !1,
        showKeyIfEmpty: !1,
        nsseparator: ":",
        keyseparator: ".",
        selectorAttr: "data-i18n",
        debug: !1,
        resGetPath: "locales/__lng__/__ns__.json",
        resPostPath: "locales/add/__lng__/__ns__",
        getAsync: !0,
        postAsync: !0,
        resStore: void 0,
        useLocalStorage: !1,
        localStorageExpirationTime: 6048e5,
        dynamicLoad: !1,
        sendMissing: !1,
        sendMissingTo: "fallback",
        sendType: "POST",
        interpolationPrefix: "__",
        interpolationSuffix: "__",
        defaultVariables: !1,
        reusePrefix: "$t(",
        reuseSuffix: ")",
        pluralSuffix: "_plural",
        pluralNotFound: ["plural_not_found", Math.random()].join(""),
        contextNotFound: ["context_not_found", Math.random()].join(""),
        escapeInterpolation: !1,
        indefiniteSuffix: "_indefinite",
        indefiniteNotFound: ["indefinite_not_found", Math.random()].join(""),
        setJqueryExt: !0,
        defaultValueFromContent: !0,
        useDataAttrOptions: !1,
        cookieExpirationTime: void 0,
        useCookie: !0,
        cookieName: "i18next",
        cookieDomain: void 0,
        objectTreeKeyHandler: void 0,
        postProcess: void 0,
        parseMissingKey: void 0,
        missingKeyHandler: g.postMissing,
        ajaxTimeout: 0,
        shortcutFunction: "sprintf"
    };
    var t = {
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;",
        "/": "&#x2F;"
    };
    var r = {
        create: function(e, n, t, r) {
            var a;
            if (t) {
                var o = new Date;
                o.setTime(o.getTime() + 60 * t * 1e3),
                a = "; expires=" + o.toGMTString()
            } else
                a = "";
            r = r ? "domain=" + r + ";" : "",
            document.cookie = e + "=" + n + a + ";" + r + "path=/"
        },
        read: function(e) {
            for (var n = e + "=", t = document.cookie.split(";"), r = 0; r < t.length; r++) {
                for (var a = t[r]; " " == a.charAt(0); )
                    a = a.substring(1, a.length);
                if (0 === a.indexOf(n))
                    return a.substring(n.length, a.length)
            }
            return null
        },
        remove: function(e) {
            this.create(e, "", -1)
        }
    }
      , C = {
        extend: c ? c.extend : function(e, n) {
            if (!n || "function" == typeof n)
                return e;
            for (var t in n)
                e[t] = n[t];
            return e
        }
        ,
        deepExtend: function e(n, t, r) {
            for (var a in t)
                a in n ? "string" == typeof n[a] || n[a]instanceof String || "string" == typeof t[a] || t[a]instanceof String ? r && (n[a] = t[a]) : e(n[a], t[a], r) : n[a] = t[a];
            return n
        },
        each: c ? c.each : function(e, n, t) {
            var r, a = 0, o = e.length, i = void 0 === o || "[object Array]" !== Object.prototype.toString.apply(e) || "function" == typeof e;
            if (t)
                if (i) {
                    for (r in e)
                        if (!1 === n.apply(e[r], t))
                            break
                } else
                    for (; a < o && !1 !== n.apply(e[a++], t); )
                        ;
            else if (i) {
                for (r in e)
                    if (!1 === n.call(e[r], r, e[r]))
                        break
            } else
                for (; a < o && !1 !== n.call(e[a], a, e[a++]); )
                    ;
            return e
        }
        ,
        ajax: c ? c.ajax : "undefined" != typeof document ? function(t) {
            function r(e) {
                var n = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
                e = function(e) {
                    e = e.replace(/\r\n/g, "\n");
                    for (var n = "", t = 0; t < e.length; t++) {
                        var r = e.charCodeAt(t);
                        r < 128 ? n += String.fromCharCode(r) : (127 < r && r < 2048 ? n += String.fromCharCode(r >> 6 | 192) : (n += String.fromCharCode(r >> 12 | 224),
                        n += String.fromCharCode(r >> 6 & 63 | 128)),
                        n += String.fromCharCode(63 & r | 128))
                    }
                    return n
                }(e);
                for (var t, r, a, o, i, s, l, u = "", f = 0; o = (t = e.charCodeAt(f++)) >> 2,
                i = (3 & t) << 4 | (r = e.charCodeAt(f++)) >> 4,
                s = (15 & r) << 2 | (a = e.charCodeAt(f++)) >> 6,
                l = 63 & a,
                isNaN(r) ? s = l = 64 : isNaN(a) && (l = 64),
                u += n.charAt(o) + n.charAt(i) + n.charAt(s) + n.charAt(l),
                t = r = a = "",
                o = i = s = l = "",
                f < e.length; )
                    ;
                return u
            }
            var u = function(r, a, o, i) {
                "function" == typeof o && (i = o,
                o = {}),
                o.cache = o.cache || !1,
                o.data = o.data || {},
                o.headers = o.headers || {},
                o.jsonp = o.jsonp || !1,
                o.async = void 0 === o.async || o.async;
                var s, l = function() {
                    for (var e = arguments[0], n = 1; n < arguments.length; n++) {
                        var t = arguments[n];
                        for (var r in t)
                            t.hasOwnProperty(r) && (e[r] = t[r])
                    }
                    return e
                }({
                    accept: "*/*",
                    "content-type": "application/x-www-form-urlencoded;charset=UTF-8"
                }, u.headers, o.headers);
                if (s = "application/json" === l["content-type"] ? JSON.stringify(o.data) : function(e) {
                    if ("string" == typeof e)
                        return e;
                    var n = [];
                    for (var t in e)
                        e.hasOwnProperty(t) && n.push(encodeURIComponent(t) + "=" + encodeURIComponent(e[t]));
                    return n.join("&")
                }(o.data),
                "GET" === r) {
                    var e = [];
                    if (s && (e.push(s),
                    s = null),
                    o.cache || e.push("_=" + (new Date).getTime()),
                    o.jsonp && (e.push("callback=" + o.jsonp),
                    e.push("jsonp=" + o.jsonp)),
                    1 < (e = e.join("&")).length && (-1 < a.indexOf("?") ? a += "&" + e : a += "?" + e),
                    o.jsonp) {
                        var n = document.getElementsByTagName("head")[0]
                          , t = document.createElement("script");
                        return t.type = "text/javascript",
                        t.src = a,
                        void n.appendChild(t)
                    }
                }
                !function(n) {
                    if (window.XMLHttpRequest)
                        return n(null, new XMLHttpRequest);
                    if (window.ActiveXObject)
                        try {
                            return n(null, new ActiveXObject("Msxml2.XMLHTTP"))
                        } catch (e) {
                            return n(null, new ActiveXObject("Microsoft.XMLHTTP"))
                        }
                    n(new Error)
                }(function(e, n) {
                    if (e)
                        return i(e);
                    for (var t in n.open(r, a, o.async),
                    l)
                        l.hasOwnProperty(t) && n.setRequestHeader(t, l[t]);
                    n.onreadystatechange = function() {
                        if (4 === n.readyState) {
                            var e = n.responseText || "";
                            if (!i)
                                return;
                            i(n.status, {
                                text: function() {
                                    return e
                                },
                                json: function() {
                                    try {
                                        return JSON.parse(e)
                                    } catch (e) {
                                        return C.error("Can not parse JSON. URL: " + a),
                                        {}
                                    }
                                }
                            })
                        }
                    }
                    ,
                    n.send(s)
                })
            };
            ({
                authBasic: function(e, n) {
                    u.headers.Authorization = "Basic " + r(e + ":" + n)
                },
                connect: function(e, n, t) {
                    return u("CONNECT", e, n, t)
                },
                del: function(e, n, t) {
                    return u("DELETE", e, n, t)
                },
                get: function(e, n, t) {
                    return u("GET", e, n, t)
                },
                head: function(e, n, t) {
                    return u("HEAD", e, n, t)
                },
                headers: function(e) {
                    u.headers = e || {}
                },
                isAllowed: function(e, t, r) {
                    this.options(e, function(e, n) {
                        r(-1 !== n.text().indexOf(t))
                    })
                },
                options: function(e, n, t) {
                    return u("OPTIONS", e, n, t)
                },
                patch: function(e, n, t) {
                    return u("PATCH", e, n, t)
                },
                post: function(e, n, t) {
                    return u("POST", e, n, t)
                },
                put: function(e, n, t) {
                    return u("PUT", e, n, t)
                },
                trace: function(e, n, t) {
                    return u("TRACE", e, n, t)
                }
            })[t.type ? t.type.toLowerCase() : "get"](t.url, t, function(e, n) {
                200 === e || 0 === e && n.text() ? t.success(n.json(), e, null) : t.error(n.text(), e, null)
            })
        }
        : function() {}
        ,
        cookie: "undefined" != typeof document ? r : {
            create: function(e, n, t, r) {},
            read: function(e) {
                return null
            },
            remove: function(e) {}
        },
        detectLanguage: function() {
            var r, a = T.lngWhitelist || [], o = [];
            "undefined" != typeof window && function() {
                for (var e = window.location.search.substring(1).split("&"), n = 0; n < e.length; n++) {
                    var t = e[n].indexOf("=");
                    if (0 < t)
                        e[n].substring(0, t) == T.detectLngQS && o.push(e[n].substring(t + 1))
                }
            }();
            if (T.useCookie && "undefined" != typeof document) {
                var e = C.cookie.read(T.cookieName);
                e && o.push(e)
            }
            if (T.detectLngFromLocalStorage && "undefined" != typeof window && window.localStorage) {
                var n = C.localStorage.getItem("i18next_lng");
                n && o.push(n)
            }
            if ("undefined" != typeof navigator) {
                if (navigator.languages)
                    for (var t = 0; t < navigator.languages.length; t++)
                        o.push(navigator.languages[t]);
                navigator.userLanguage && o.push(navigator.userLanguage),
                navigator.language && o.push(navigator.language)
            }
            (function() {
                for (var e = 0; e < o.length; e++) {
                    var n = o[e];
                    if (-1 < n.indexOf("-")) {
                        var t = n.split("-");
                        n = T.lowerCaseLng ? t[0].toLowerCase() + "-" + t[1].toLowerCase() : t[0].toLowerCase() + "-" + t[1].toUpperCase()
                    }
                    if (0 === a.length || -1 < a.indexOf(n)) {
                        r = n;
                        break
                    }
                }
            }
            )(),
            r = r || T.fallbackLng[0];
            return r
        },
        escape: function(e) {
            return "string" == typeof e ? e.replace(/[&<>"'\/]/g, function(e) {
                return t[e]
            }) : e
        },
        log: function(e) {
            T.debug && "undefined" != typeof console && console.log(e)
        },
        error: function(e) {
            "undefined" != typeof console && console.error(e)
        },
        getCountyIndexOfLng: function(e) {
            var n = 0;
            return "nb-NO" !== e && "nn-NO" !== e && "nb-no" !== e && "nn-no" !== e || (n = 1),
            n
        },
        toLanguages: function(e, n) {
            var t = this.log;
            function r(e) {
                var n = e;
                if ("string" == typeof e && -1 < e.indexOf("-")) {
                    var t = e.split("-");
                    n = T.lowerCaseLng ? t[0].toLowerCase() + "-" + t[1].toLowerCase() : t[0].toLowerCase() + "-" + t[1].toUpperCase()
                } else
                    n = T.lowerCaseLng ? e.toLowerCase() : e;
                return n
            }
            "string" == typeof (n = n || T.fallbackLng) && (n = [n]);
            function a(e) {
                !i || -1 < i.indexOf(e) ? o.push(e) : t("rejecting non-whitelisted language: " + e)
            }
            var o = []
              , i = T.lngWhitelist || !1;
            if ("string" == typeof e && -1 < e.indexOf("-")) {
                var s = e.split("-");
                "unspecific" !== T.load && a(r(e)),
                "current" !== T.load && a(r(s[this.getCountyIndexOfLng(e)]))
            } else
                a(r(e));
            for (var l = 0; l < n.length; l++)
                -1 === o.indexOf(n[l]) && n[l] && o.push(r(n[l]));
            return o
        },
        regexEscape: function(e) {
            return e.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&")
        },
        regexReplacementEscape: function(e) {
            return "string" == typeof e ? e.replace(/\$/g, "$$$$") : e
        },
        localStorage: {
            setItem: function(n, e) {
                if (window.localStorage)
                    try {
                        window.localStorage.setItem(n, e)
                    } catch (e) {
                        C.log('failed to set value for key "' + n + '" to localStorage.')
                    }
            },
            getItem: function(n, e) {
                if (window.localStorage)
                    try {
                        return window.localStorage.getItem(n, e)
                    } catch (e) {
                        return void C.log('failed to get value for key "' + n + '" from localStorage.')
                    }
            }
        }
    };
    function a(e, t) {
        "function" == typeof e && (t = e,
        e = {}),
        e = e || {},
        C.extend(T, e),
        delete T.fixLng,
        T.functions && (delete T.functions,
        C.extend(C, e.functions)),
        "string" == typeof T.ns && (T.ns = {
            namespaces: [T.ns],
            defaultNs: T.ns
        }),
        "string" == typeof T.fallbackNS && (T.fallbackNS = [T.fallbackNS]),
        "string" != typeof T.fallbackLng && "boolean" != typeof T.fallbackLng || (T.fallbackLng = [T.fallbackLng]),
        T.interpolationPrefixEscaped = C.regexEscape(T.interpolationPrefix),
        T.interpolationSuffixEscaped = C.regexEscape(T.interpolationSuffix),
        T.lng || (T.lng = C.detectLanguage()),
        P = C.toLanguages(T.lng),
        f = P[0],
        C.log("currentLng set to: " + f),
        T.useCookie && C.cookie.read(T.cookieName) !== f && C.cookie.create(T.cookieName, f, T.cookieExpirationTime, T.cookieDomain),
        T.detectLngFromLocalStorage && "undefined" != typeof document && window.localStorage && C.localStorage.setItem("i18next_lng", f);
        var r, a = E;
        if (e.fixLng && ((a = function(e, n) {
            return (n = n || {}).lng = n.lng || a.lng,
            E(e, n)
        }
        ).lng = f),
        V.setCurrentLng(f),
        c && T.setJqueryExt ? h() : y(),
        c && c.Deferred && (r = c.Deferred()),
        T.resStore)
            return j = T.resStore,
            d = !0,
            t && t(null, a),
            r && r.resolve(a),
            r ? r.promise() : void 0;
        var n = C.toLanguages(T.lng);
        "string" == typeof T.preload && (T.preload = [T.preload]);
        for (var o = 0, i = T.preload.length; o < i; o++)
            for (var s = C.toLanguages(T.preload[o]), l = 0, u = s.length; l < u; l++)
                n.indexOf(s[l]) < 0 && n.push(s[l]);
        return N.sync.load(n, T, function(e, n) {
            j = n,
            d = !0,
            t && t(e, a),
            r && (e ? r.reject : r.resolve)(e || a)
        }),
        r ? r.promise() : void 0
    }
    function o(e, n, t, r) {
        "string" != typeof n ? (resource = n,
        n = T.ns.defaultNs) : T.ns.namespaces.indexOf(n) < 0 && T.ns.namespaces.push(n),
        j[e] = j[e] || {},
        j[e][n] = j[e][n] || {};
        for (var a = t.split(T.keyseparator), o = 0, i = j[e][n]; a[o]; )
            o == a.length - 1 ? i[a[o]] = r : (null == i[a[o]] && (i[a[o]] = {}),
            i = i[a[o]]),
            o++;
        T.useLocalStorage && g._storeLocal(j)
    }
    function i(n, o) {
        var e = {
            dynamicLoad: T.dynamicLoad,
            resGetPath: T.resGetPath,
            getAsync: T.getAsync,
            customLoad: T.customLoad,
            ns: {
                namespaces: n,
                defaultNs: ""
            }
        }
          , t = C.toLanguages(T.lng);
        "string" == typeof T.preload && (T.preload = [T.preload]);
        for (var r = 0, a = T.preload.length; r < a; r++)
            for (var i = C.toLanguages(T.preload[r]), s = 0, l = i.length; s < l; s++)
                t.indexOf(i[s]) < 0 && t.push(i[s]);
        for (var u = [], f = 0, c = t.length; f < c; f++) {
            var p = !1
              , d = j[t[f]];
            if (d)
                for (var g = 0, h = n.length; g < h; g++)
                    d[n[g]] || (p = !0);
            else
                p = !0;
            p && u.push(t[f])
        }
        u.length ? N.sync._fetch(u, e, function(e, r) {
            var a = n.length * u.length;
            C.each(n, function(e, t) {
                T.ns.namespaces.indexOf(t) < 0 && T.ns.namespaces.push(t),
                C.each(u, function(e, n) {
                    j[n] = j[n] || {},
                    j[n][t] = r[n][t],
                    0 === --a && o && (T.useLocalStorage && N.sync._storeLocal(j),
                    o())
                })
            })
        }) : o && o()
    }
    function s(e, n, t) {
        return (n = "function" == typeof n ? (t = n,
        {}) : n || {}).lng = e,
        a(n, t)
    }
    function h() {
        function s(e, n, t) {
            if (0 !== n.length) {
                var r, a = "text";
                if (0 === n.indexOf("[")) {
                    var o = n.split("]");
                    n = o[1],
                    a = o[0].substr(1, o[0].length - 1)
                }
                if (n.indexOf(";") === n.length - 1 && (n = n.substr(0, n.length - 2)),
                "html" === a)
                    r = T.defaultValueFromContent ? c.extend({
                        defaultValue: e.html()
                    }, t) : t,
                    e.html(c.t(n, r));
                else if ("text" === a)
                    r = T.defaultValueFromContent ? c.extend({
                        defaultValue: e.text()
                    }, t) : t,
                    e.text(c.t(n, r));
                else if ("prepend" === a)
                    r = T.defaultValueFromContent ? c.extend({
                        defaultValue: e.html()
                    }, t) : t,
                    e.prepend(c.t(n, r));
                else if ("append" === a)
                    r = T.defaultValueFromContent ? c.extend({
                        defaultValue: e.html()
                    }, t) : t,
                    e.append(c.t(n, r));
                else if (0 === a.indexOf("data-")) {
                    var i = a.substr("data-".length);
                    r = T.defaultValueFromContent ? c.extend({
                        defaultValue: e.data(i)
                    }, t) : t;
                    var s = c.t(n, r);
                    e.data(i, s),
                    e.attr(a, s)
                } else
                    r = T.defaultValueFromContent ? c.extend({
                        defaultValue: e.attr(a)
                    }, t) : t,
                    e.attr(a, c.t(n, r))
            }
        }
        function n(e, t) {
            var n = e.attr(T.selectorAttr);
            if (n || void 0 === n || !1 === n || (n = e.text() || e.val()),
            n) {
                var r = e
                  , a = e.data("i18n-target");
                if (a && (r = e.find(a) || e),
                t || !0 !== T.useDataAttrOptions || (t = e.data("i18n-options")),
                t = t || {},
                0 <= n.indexOf(";")) {
                    var o = n.split(";");
                    c.each(o, function(e, n) {
                        "" !== n && s(r, n, t)
                    })
                } else
                    s(r, n, t);
                if (!0 === T.useDataAttrOptions) {
                    var i = c.extend({
                        lng: "non",
                        lngs: [],
                        _origLng: "non"
                    }, t);
                    delete i.lng,
                    delete i.lngs,
                    delete i._origLng,
                    e.data("i18n-options", i)
                }
            }
        }
        c.t = c.t || E,
        c.fn.i18n = function(e) {
            return this.each(function() {
                n(c(this), e),
                c(this).find("[" + T.selectorAttr + "]").each(function() {
                    n(c(this), e)
                })
            })
        }
    }
    function y() {
        function l(e, n, t) {
            if (0 !== n.length) {
                var r = "text";
                if (0 === n.indexOf("[")) {
                    var a = n.split("]");
                    n = a[1],
                    r = a[0].substr(1, a[0].length - 1)
                }
                n.indexOf(";") === n.length - 1 && (n = n.substr(0, n.length - 2)),
                "html" === r ? e.innerHTML = E(n, t) : "text" === r ? e.textContent = E(n, t) : "prepend" === r ? e.insertAdjacentHTML(E(n, t), "afterbegin") : "append" === r ? e.insertAdjacentHTML(E(n, t), "beforeend") : e.setAttribute(r, E(n, t))
            }
        }
        function o(e, n) {
            var t = e.getAttribute(T.selectorAttr);
            if (t || void 0 === t || !1 === t || (t = e.textContent || e.value),
            t) {
                var r = e
                  , a = e.getAttribute("i18n-target");
                if (a && (r = e.querySelector(a) || e),
                0 <= t.indexOf(";"))
                    for (var o = t.split(";"), i = 0, s = o.length; i < s; i++)
                        "" !== o[i] && l(r, o[i], n);
                else
                    l(r, t, n)
            }
        }
        N.translateObject = function(e, n) {
            for (var t = e.querySelectorAll("[" + T.selectorAttr + "]"), r = 0, a = t.length; r < a; r++)
                o(t[r], n)
        }
    }
    function _(e, n, t, r) {
        if (!e)
            return e;
        if (r = r || n,
        e.indexOf(r.interpolationPrefix || T.interpolationPrefix) < 0)
            return e;
        var a = r.interpolationPrefix ? C.regexEscape(r.interpolationPrefix) : T.interpolationPrefixEscaped
          , o = r.interpolationSuffix ? C.regexEscape(r.interpolationSuffix) : T.interpolationSuffixEscaped
          , i = r.keyseparator || T.keyseparator
          , s = n.replace && "object" == typeof n.replace ? n.replace : n
          , l = new RegExp([a, "(.+?)", "(HTML)?", o].join(""),"g")
          , u = r.escapeInterpolation || T.escapeInterpolation;
        return e.replace(l, function(e, n, t) {
            for (var r = s, a = n; 0 <= a.indexOf(i) && "object" == typeof r && r; ) {
                var o = a.slice(0, a.indexOf(i));
                a = a.slice(a.indexOf(i) + 1),
                r = r[o]
            }
            if (r && "object" == typeof r && r.hasOwnProperty(a)) {
                r[a];
                return u && !t ? C.escape(r[a]) : r[a]
            }
            return e
        })
    }
    function A(e, n) {
        var t = C.extend({}, n);
        for (delete t.postProcess,
        delete t.isFallbackLookup; -1 != e.indexOf(T.reusePrefix) && !(++p > T.maxRecursion); ) {
            var r = e.lastIndexOf(T.reusePrefix)
              , a = e.indexOf(T.reuseSuffix, r) + T.reuseSuffix.length
              , o = e.substring(r, a)
              , i = o.replace(T.reusePrefix, "").replace(T.reuseSuffix, "");
            if (a <= r)
                return C.error("there is an missing closing in following translation value", e),
                "";
            if (-1 != i.indexOf(",")) {
                var s = i.indexOf(",");
                if (-1 != i.indexOf("{", s) && -1 != i.indexOf("}", s)) {
                    var l = i.indexOf("{", s)
                      , u = i.indexOf("}", l) + "}".length;
                    try {
                        t = C.extend(t, JSON.parse(i.substring(l, u))),
                        i = i.substring(0, s)
                    } catch (e) {}
                }
            }
            var f = R(i, t);
            e = e.replace(o, C.regexReplacementEscape(f))
        }
        return e
    }
    function M(e) {
        return void 0 !== e.count && "string" != typeof e.count
    }
    function m(e, n) {
        var t = F(e, n = n || {})
          , r = I(e, n);
        return void 0 !== r || r === t
    }
    function E(e, n) {
        return d ? (p = 0,
        R.apply(null, arguments)) : (C.log("i18next not finished initialization. you might have called t function before loading resources finished."),
        n && n.defaultValue ? n.detaultValue : "")
    }
    function F(e, n) {
        return void 0 !== n.defaultValue ? n.defaultValue : e
    }
    function R(e, n) {
        if (void 0 !== n && "object" != typeof n ? "sprintf" === T.shortcutFunction ? n = function() {
            for (var e = [], n = 1; n < arguments.length; n++)
                e.push(arguments[n]);
            return {
                postProcess: "sprintf",
                sprintf: e
            }
        }
        .apply(null, arguments) : "defaultValue" === T.shortcutFunction && (n = {
            defaultValue: n
        }) : n = n || {},
        "object" == typeof T.defaultVariables && (n = C.extend({}, T.defaultVariables, n)),
        null == e || "" === e)
            return "";
        "number" == typeof e && (e = String(e)),
        "string" == typeof e && (e = [e]);
        var t = e[0];
        if (1 < e.length)
            for (var r = 0; r < e.length && !m(t = e[r], n); r++)
                ;
        var a, o, i, s, l = F(t, n), u = I(t, n), f = n.nsseparator || T.nsseparator, c = n.lng ? C.toLanguages(n.lng, n.fallbackLng) : P, p = n.ns || T.ns.defaultNs;
        if (-1 < t.indexOf(f) && (p = (a = t.split(f))[0],
        t = a[1]),
        void 0 === u && T.sendMissing && "function" == typeof T.missingKeyHandler && (n.lng ? T.missingKeyHandler(c[0], p, t, l, c) : T.missingKeyHandler(T.lng, p, t, l, c)),
        o = "string" == typeof T.postProcess && "" !== T.postProcess ? [T.postProcess] : "array" == typeof T.postProcess || "object" == typeof T.postProcess ? T.postProcess : [],
        "string" == typeof n.postProcess && "" !== n.postProcess ? o = o.concat([n.postProcess]) : "array" != typeof n.postProcess && "object" != typeof n.postProcess || (o = o.concat(n.postProcess)),
        void 0 !== u && o.length)
            for (s = 0; s < o.length; s += 1)
                i = o[s],
                b[i] && (u = b[i](u, t, n));
        var d = l;
        if (-1 < l.indexOf(f) && (d = (a = l.split(f))[1]),
        d === t && T.parseMissingKey && (l = T.parseMissingKey(l)),
        void 0 === u && (l = A(l = _(l, n), n),
        o.length))
            for (u = F(t, n),
            s = 0; s < o.length; s += 1)
                i = o[s],
                b[i] && (u = b[i](u, t, n));
        return void 0 !== u ? u : l
    }
    function I(n, t) {
        var e, r, a = F(n, t = t || {}), o = P;
        if (!j)
            return a;
        if ("cimode" === o[0].toLowerCase())
            return a;
        if (t.lngs && (o = t.lngs),
        t.lng && (o = C.toLanguages(t.lng, t.fallbackLng),
        !j[o[0]])) {
            var i = T.getAsync;
            T.getAsync = !1,
            N.sync.load(o, T, function(e, n) {
                C.extend(j, n),
                T.getAsync = i
            })
        }
        var s, l = t.ns || T.ns.defaultNs, u = t.nsseparator || T.nsseparator;
        if (-1 < n.indexOf(u)) {
            var f = n.split(u);
            l = f[0],
            n = f[1]
        }
        if (function(e) {
            return e.context && ("string" == typeof e.context || "number" == typeof e.context)
        }(t) && (delete (e = C.extend({}, t)).context,
        e.defaultValue = T.contextNotFound,
        (r = E(l + u + n + "_" + t.context, e)) != T.contextNotFound))
            return _(r, {
                context: t.context
            });
        if (M(t, o[0])) {
            var c;
            if (delete (e = C.extend({
                lngs: [o[0]]
            }, t)).count,
            e._origLng = e._origLng || e.lng || o[0],
            delete e.lng,
            e.defaultValue = T.pluralNotFound,
            V.needsPlural(o[0], t.count)) {
                c = l + u + n + T.pluralSuffix;
                var p = V.get(o[0], t.count);
                0 <= p ? c = c + "_" + p : 1 === p && (c = l + u + n)
            } else
                c = l + u + n;
            if ((r = E(c, e)) != T.pluralNotFound)
                return _(r, {
                    count: t.count,
                    interpolationPrefix: t.interpolationPrefix,
                    interpolationSuffix: t.interpolationSuffix
                });
            if (!(1 < o.length))
                return e.lng = e._origLng,
                delete e._origLng,
                _(r = E(l + u + n, e), {
                    count: t.count,
                    interpolationPrefix: t.interpolationPrefix,
                    interpolationSuffix: t.interpolationSuffix
                });
            var d = o.slice();
            if (d.shift(),
            (t = C.extend(t, {
                lngs: d
            }))._origLng = e._origLng,
            delete t.lng,
            (r = E(l + u + n, t)) != T.pluralNotFound)
                return r
        }
        if (function(e) {
            return void 0 !== e.indefinite_article && "string" != typeof e.indefinite_article && e.indefinite_article
        }(t)) {
            var g = C.extend({}, t);
            if (delete g.indefinite_article,
            g.defaultValue = T.indefiniteNotFound,
            (r = E(l + u + n + (t.count && !M(t, o[0]) || !t.count ? T.indefiniteSuffix : ""), g)) != T.indefiniteNotFound)
                return r
        }
        for (var h = t.keyseparator || T.keyseparator, y = n.split(h), m = 0, v = o.length; m < v && void 0 === s; m++) {
            for (var x = o[m], b = 0, k = j[x] && j[x][l]; y[b]; )
                k = k && k[y[b]],
                b++;
            if (void 0 !== k && (!T.showKeyIfEmpty || "" !== k)) {
                var L = Object.prototype.toString.apply(k);
                if ("string" == typeof k)
                    k = A(k = _(k, t), t);
                else if ("[object Array]" !== L || T.returnObjectTrees || t.returnObjectTrees) {
                    if (null === k && !0 === T.fallbackOnNull)
                        k = void 0;
                    else if (null !== k)
                        if (T.returnObjectTrees || t.returnObjectTrees) {
                            if ("[object Number]" !== L && "[object Function]" !== L && "[object RegExp]" !== L) {
                                var S = "[object Array]" === L ? [] : {};
                                C.each(k, function(e) {
                                    S[e] = R(l + u + n + h + e, t)
                                }),
                                k = S
                            }
                        } else
                            T.objectTreeKeyHandler && "function" == typeof T.objectTreeKeyHandler ? k = T.objectTreeKeyHandler(n, k, x, l, t) : (k = "key '" + l + ":" + n + " (" + x + ")' returned an object instead of string.",
                            C.log(k))
                } else
                    k = A(k = _(k = k.join("\n"), t), t);
                "string" == typeof k && "" === k.trim() && !0 === T.fallbackOnEmpty && (k = void 0),
                s = k
            }
        }
        if (void 0 === s && !t.isFallbackLookup && (!0 === T.fallbackToDefaultNS || T.fallbackNS && 0 < T.fallbackNS.length)) {
            if (t.isFallbackLookup = !0,
            T.fallbackNS.length)
                for (var w = 0, O = T.fallbackNS.length; w < O; w++) {
                    if ((s = I(T.fallbackNS[w] + u + n, t)) || "" === s && !1 === T.fallbackOnEmpty)
                        if ((-1 < s.indexOf(u) ? s.split(u)[1] : s) !== (-1 < a.indexOf(u) ? a.split(u)[1] : a))
                            break
                }
            else
                t.ns = T.ns.defaultNs,
                s = I(n, t);
            t.isFallbackLookup = !1
        }
        return s
    }
    C.applyReplacement = _;
    function l(e, n) {
        b[e] = n
    }
    var u, v = [["ach", "Acholi", [1, 2], 1], ["af", "Afrikaans", [1, 2], 2], ["ak", "Akan", [1, 2], 1], ["am", "Amharic", [1, 2], 1], ["an", "Aragonese", [1, 2], 2], ["ar", "Arabic", [0, 1, 2, 3, 11, 100], 5], ["arn", "Mapudungun", [1, 2], 1], ["ast", "Asturian", [1, 2], 2], ["ay", "Aymará", [1], 3], ["az", "Azerbaijani", [1, 2], 2], ["be", "Belarusian", [1, 2, 5], 4], ["bg", "Bulgarian", [1, 2], 2], ["bn", "Bengali", [1, 2], 2], ["bo", "Tibetan", [1], 3], ["br", "Breton", [1, 2], 1], ["bs", "Bosnian", [1, 2, 5], 4], ["ca", "Catalan", [1, 2], 2], ["cgg", "Chiga", [1], 3], ["cs", "Czech", [1, 2, 5], 6], ["csb", "Kashubian", [1, 2, 5], 7], ["cy", "Welsh", [1, 2, 3, 8], 8], ["da", "Danish", [1, 2], 2], ["de", "German", [1, 2], 2], ["dev", "Development Fallback", [1, 2], 2], ["dz", "Dzongkha", [1], 3], ["el", "Greek", [1, 2], 2], ["en", "English", [1, 2], 2], ["eo", "Esperanto", [1, 2], 2], ["es", "Spanish", [1, 2], 2], ["es_ar", "Argentinean Spanish", [1, 2], 2], ["et", "Estonian", [1, 2], 2], ["eu", "Basque", [1, 2], 2], ["fa", "Persian", [1], 3], ["fi", "Finnish", [1, 2], 2], ["fil", "Filipino", [1, 2], 1], ["fo", "Faroese", [1, 2], 2], ["fr", "French", [1, 2], 9], ["fur", "Friulian", [1, 2], 2], ["fy", "Frisian", [1, 2], 2], ["ga", "Irish", [1, 2, 3, 7, 11], 10], ["gd", "Scottish Gaelic", [1, 2, 3, 20], 11], ["gl", "Galician", [1, 2], 2], ["gu", "Gujarati", [1, 2], 2], ["gun", "Gun", [1, 2], 1], ["ha", "Hausa", [1, 2], 2], ["he", "Hebrew", [1, 2], 2], ["hi", "Hindi", [1, 2], 2], ["hr", "Croatian", [1, 2, 5], 4], ["hu", "Hungarian", [1, 2], 2], ["hy", "Armenian", [1, 2], 2], ["ia", "Interlingua", [1, 2], 2], ["id", "Indonesian", [1], 3], ["is", "Icelandic", [1, 2], 12], ["it", "Italian", [1, 2], 2], ["ja", "Japanese", [1], 3], ["jbo", "Lojban", [1], 3], ["jv", "Javanese", [0, 1], 13], ["ka", "Georgian", [1], 3], ["kk", "Kazakh", [1], 3], ["km", "Khmer", [1], 3], ["kn", "Kannada", [1, 2], 2], ["ko", "Korean", [1], 3], ["ku", "Kurdish", [1, 2], 2], ["kw", "Cornish", [1, 2, 3, 4], 14], ["ky", "Kyrgyz", [1], 3], ["lb", "Letzeburgesch", [1, 2], 2], ["ln", "Lingala", [1, 2], 1], ["lo", "Lao", [1], 3], ["lt", "Lithuanian", [1, 2, 10], 15], ["lv", "Latvian", [1, 2, 0], 16], ["mai", "Maithili", [1, 2], 2], ["mfe", "Mauritian Creole", [1, 2], 1], ["mg", "Malagasy", [1, 2], 1], ["mi", "Maori", [1, 2], 1], ["mk", "Macedonian", [1, 2], 17], ["ml", "Malayalam", [1, 2], 2], ["mn", "Mongolian", [1, 2], 2], ["mnk", "Mandinka", [0, 1, 2], 18], ["mr", "Marathi", [1, 2], 2], ["ms", "Malay", [1], 3], ["mt", "Maltese", [1, 2, 11, 20], 19], ["nah", "Nahuatl", [1, 2], 2], ["nap", "Neapolitan", [1, 2], 2], ["nb", "Norwegian Bokmal", [1, 2], 2], ["ne", "Nepali", [1, 2], 2], ["nl", "Dutch", [1, 2], 2], ["nn", "Norwegian Nynorsk", [1, 2], 2], ["no", "Norwegian", [1, 2], 2], ["nso", "Northern Sotho", [1, 2], 2], ["oc", "Occitan", [1, 2], 1], ["or", "Oriya", [2, 1], 2], ["pa", "Punjabi", [1, 2], 2], ["pap", "Papiamento", [1, 2], 2], ["pl", "Polish", [1, 2, 5], 7], ["pms", "Piemontese", [1, 2], 2], ["ps", "Pashto", [1, 2], 2], ["pt", "Portuguese", [1, 2], 2], ["pt_br", "Brazilian Portuguese", [1, 2], 2], ["rm", "Romansh", [1, 2], 2], ["ro", "Romanian", [1, 2, 20], 20], ["ru", "Russian", [1, 2, 5], 4], ["sah", "Yakut", [1], 3], ["sco", "Scots", [1, 2], 2], ["se", "Northern Sami", [1, 2], 2], ["si", "Sinhala", [1, 2], 2], ["sk", "Slovak", [1, 2, 5], 6], ["sl", "Slovenian", [5, 1, 2, 3], 21], ["so", "Somali", [1, 2], 2], ["son", "Songhay", [1, 2], 2], ["sq", "Albanian", [1, 2], 2], ["sr", "Serbian", [1, 2, 5], 4], ["su", "Sundanese", [1], 3], ["sv", "Swedish", [1, 2], 2], ["sw", "Swahili", [1, 2], 2], ["ta", "Tamil", [1, 2], 2], ["te", "Telugu", [1, 2], 2], ["tg", "Tajik", [1, 2], 1], ["th", "Thai", [1], 3], ["ti", "Tigrinya", [1, 2], 1], ["tk", "Turkmen", [1, 2], 2], ["tr", "Turkish", [1, 2], 1], ["tt", "Tatar", [1], 3], ["ug", "Uyghur", [1], 3], ["uk", "Ukrainian", [1, 2, 5], 4], ["ur", "Urdu", [1, 2], 2], ["uz", "Uzbek", [1, 2], 1], ["vi", "Vietnamese", [1], 3], ["wa", "Walloon", [1, 2], 1], ["wo", "Wolof", [1], 3], ["yo", "Yoruba", [1, 2], 2], ["zh", "Chinese", [1], 3]], x = {
        1: function(e) {
            return Number(1 < e)
        },
        2: function(e) {
            return Number(1 != e)
        },
        3: function(e) {
            return 0
        },
        4: function(e) {
            return Number(e % 10 == 1 && e % 100 != 11 ? 0 : 2 <= e % 10 && e % 10 <= 4 && (e % 100 < 10 || 20 <= e % 100) ? 1 : 2)
        },
        5: function(e) {
            return Number(0 === e ? 0 : 1 == e ? 1 : 2 == e ? 2 : 3 <= e % 100 && e % 100 <= 10 ? 3 : 11 <= e % 100 ? 4 : 5)
        },
        6: function(e) {
            return Number(1 == e ? 0 : 2 <= e && e <= 4 ? 1 : 2)
        },
        7: function(e) {
            return Number(1 == e ? 0 : 2 <= e % 10 && e % 10 <= 4 && (e % 100 < 10 || 20 <= e % 100) ? 1 : 2)
        },
        8: function(e) {
            return Number(1 == e ? 0 : 2 == e ? 1 : 8 != e && 11 != e ? 2 : 3)
        },
        9: function(e) {
            return Number(2 <= e)
        },
        10: function(e) {
            return Number(1 == e ? 0 : 2 == e ? 1 : e < 7 ? 2 : e < 11 ? 3 : 4)
        },
        11: function(e) {
            return Number(1 == e || 11 == e ? 0 : 2 == e || 12 == e ? 1 : 2 < e && e < 20 ? 2 : 3)
        },
        12: function(e) {
            return Number(e % 10 != 1 || e % 100 == 11)
        },
        13: function(e) {
            return Number(0 !== e)
        },
        14: function(e) {
            return Number(1 == e ? 0 : 2 == e ? 1 : 3 == e ? 2 : 3)
        },
        15: function(e) {
            return Number(e % 10 == 1 && e % 100 != 11 ? 0 : 2 <= e % 10 && (e % 100 < 10 || 20 <= e % 100) ? 1 : 2)
        },
        16: function(e) {
            return Number(e % 10 == 1 && e % 100 != 11 ? 0 : 0 !== e ? 1 : 2)
        },
        17: function(e) {
            return Number(1 == e || e % 10 == 1 ? 0 : 1)
        },
        18: function(e) {
            return Number(0 == e ? 0 : 1 == e ? 1 : 2)
        },
        19: function(e) {
            return Number(1 == e ? 0 : 0 === e || 1 < e % 100 && e % 100 < 11 ? 1 : 10 < e % 100 && e % 100 < 20 ? 2 : 3)
        },
        20: function(e) {
            return Number(1 == e ? 0 : 0 === e || 0 < e % 100 && e % 100 < 20 ? 1 : 2)
        },
        21: function(e) {
            return Number(e % 100 == 1 ? 1 : e % 100 == 2 ? 2 : e % 100 == 3 || e % 100 == 4 ? 3 : 0)
        }
    }, V = {
        rules: function() {
            var e, n = {};
            for (e = v.length; e--; )
                n[v[e][0]] = {
                    name: v[e][1],
                    numbers: v[e][2],
                    plurals: x[v[e][3]]
                };
            return n
        }(),
        addRule: function(e, n) {
            V.rules[e] = n
        },
        setCurrentLng: function(e) {
            if (!V.currentRule || V.currentRule.lng !== e) {
                var n = e.split("-");
                V.currentRule = {
                    lng: e,
                    rule: V.rules[n[0]]
                }
            }
        },
        needsPlural: function(e, n) {
            var t, r = e.split("-");
            return !((t = V.currentRule && V.currentRule.lng === e ? V.currentRule.rule : V.rules[r[C.getCountyIndexOfLng(e)]]) && t.numbers.length <= 1) && 1 !== this.get(e, n)
        },
        get: function(o, e) {
            return function(e, n) {
                var t;
                if (t = V.currentRule && V.currentRule.lng === o ? V.currentRule.rule : V.rules[e]) {
                    var r;
                    r = t.noAbs ? t.plurals(n) : t.plurals(Math.abs(n));
                    var a = t.numbers[r];
                    return 2 === t.numbers.length && 1 === t.numbers[0] && (2 === a ? a = -1 : 1 === a && (a = 1)),
                    a
                }
                return 1 === n ? "1" : "-1"
            }(o.split("-")[C.getCountyIndexOfLng(o)], e)
        }
    }, b = {}, k = ((u = function() {
        return u.cache.hasOwnProperty(arguments[0]) || (u.cache[arguments[0]] = u.parse(arguments[0])),
        u.format.call(null, u.cache[arguments[0]], arguments)
    }
    ).format = function(e, n) {
        var t, r, a, o, i, s, l, u = 1, f = e.length, c = "", p = [];
        for (r = 0; r < f; r++)
            if ("string" === (c = L(e[r])))
                p.push(e[r]);
            else if ("array" === c) {
                if ((o = e[r])[2])
                    for (t = n[u],
                    a = 0; a < o[2].length; a++) {
                        if (!t.hasOwnProperty(o[2][a]))
                            throw k('[sprintf] property "%s" does not exist', o[2][a]);
                        t = t[o[2][a]]
                    }
                else
                    t = o[1] ? n[o[1]] : n[u++];
                if (/[^s]/.test(o[8]) && "number" != L(t))
                    throw k("[sprintf] expecting number but found %s", L(t));
                switch (o[8]) {
                case "b":
                    t = t.toString(2);
                    break;
                case "c":
                    t = String.fromCharCode(t);
                    break;
                case "d":
                    t = parseInt(t, 10);
                    break;
                case "e":
                    t = o[7] ? t.toExponential(o[7]) : t.toExponential();
                    break;
                case "f":
                    t = o[7] ? parseFloat(t).toFixed(o[7]) : parseFloat(t);
                    break;
                case "o":
                    t = t.toString(8);
                    break;
                case "s":
                    t = (t = String(t)) && o[7] ? t.substring(0, o[7]) : t;
                    break;
                case "u":
                    t = Math.abs(t);
                    break;
                case "x":
                    t = t.toString(16);
                    break;
                case "X":
                    t = t.toString(16).toUpperCase()
                }
                t = /[def]/.test(o[8]) && o[3] && 0 <= t ? "+" + t : t,
                s = o[4] ? "0" == o[4] ? "0" : o[4].charAt(1) : " ",
                l = o[6] - String(t).length,
                i = o[6] ? S(s, l) : "",
                p.push(o[5] ? t + i : i + t)
            }
        return p.join("")
    }
    ,
    u.cache = {},
    u.parse = function(e) {
        for (var n = e, t = [], r = [], a = 0; n; ) {
            if (null !== (t = /^[^\x25]+/.exec(n)))
                r.push(t[0]);
            else if (null !== (t = /^\x25{2}/.exec(n)))
                r.push("%");
            else {
                if (null === (t = /^\x25(?:([1-9]\d*)\$|\(([^\)]+)\))?(\+)?(0|'[^$])?(-)?(\d+)?(?:\.(\d+))?([b-fosuxX])/.exec(n)))
                    throw "[sprintf] huh?";
                if (t[2]) {
                    a |= 1;
                    var o = []
                      , i = t[2]
                      , s = [];
                    if (null === (s = /^([a-z_][a-z_\d]*)/i.exec(i)))
                        throw "[sprintf] huh?";
                    for (o.push(s[1]); "" !== (i = i.substring(s[0].length)); )
                        if (null !== (s = /^\.([a-z_][a-z_\d]*)/i.exec(i)))
                            o.push(s[1]);
                        else {
                            if (null === (s = /^\[(\d+)\]/.exec(i)))
                                throw "[sprintf] huh?";
                            o.push(s[1])
                        }
                    t[2] = o
                } else
                    a |= 2;
                if (3 === a)
                    throw "[sprintf] mixing positional and named placeholders is not (yet) supported";
                r.push(t)
            }
            n = n.substring(t[0].length)
        }
        return r
    }
    ,
    u);
    function L(e) {
        return Object.prototype.toString.call(e).slice(8, -1).toLowerCase()
    }
    function S(e, n) {
        for (var t = []; 0 < n; t[--n] = e)
            ;
        return t.join("")
    }
    l("sprintf", function(e, n, t) {
        return t.sprintf ? "[object Array]" === Object.prototype.toString.apply(t.sprintf) ? function(e, n) {
            return n.unshift(e),
            k.apply(null, n)
        }(e, t.sprintf) : "object" == typeof t.sprintf ? k(e, t.sprintf) : e : e
    }),
    N.init = a,
    N.isInitialized = function() {
        return d
    }
    ,
    N.setLng = s,
    N.preload = function(e, n) {
        "string" == typeof e && (e = [e]);
        for (var t = 0, r = e.length; t < r; t++)
            T.preload.indexOf(e[t]) < 0 && T.preload.push(e[t]);
        return a(n)
    }
    ,
    N.addResourceBundle = function(e, n, t, r, a) {
        "string" != typeof n ? (t = n,
        n = T.ns.defaultNs) : T.ns.namespaces.indexOf(n) < 0 && T.ns.namespaces.push(n),
        j[e] = j[e] || {},
        j[e][n] = j[e][n] || {},
        r ? C.deepExtend(j[e][n], t, a) : C.extend(j[e][n], t),
        T.useLocalStorage && g._storeLocal(j)
    }
    ,
    N.hasResourceBundle = function(e, n) {
        "string" != typeof n && (n = T.ns.defaultNs),
        j[e] = j[e] || {};
        var t = j[e][n] || {}
          , r = !1;
        for (var a in t)
            t.hasOwnProperty(a) && (r = !0);
        return r
    }
    ,
    N.getResourceBundle = function(e, n) {
        return "string" != typeof n && (n = T.ns.defaultNs),
        j[e] = j[e] || {},
        C.extend({}, j[e][n])
    }
    ,
    N.addResource = o,
    N.addResources = function(e, n, t) {
        for (var r in "string" != typeof n ? (t = n,
        n = T.ns.defaultNs) : T.ns.namespaces.indexOf(n) < 0 && T.ns.namespaces.push(n),
        t)
            "string" == typeof t[r] && o(e, n, r, t[r])
    }
    ,
    N.removeResourceBundle = function(e, n) {
        "string" != typeof n && (n = T.ns.defaultNs),
        j[e] = j[e] || {},
        j[e][n] = {},
        T.useLocalStorage && g._storeLocal(j)
    }
    ,
    N.loadNamespace = function(e, n) {
        i([e], n)
    }
    ,
    N.loadNamespaces = i,
    N.setDefaultNamespace = function(e) {
        T.ns.defaultNs = e
    }
    ,
    N.t = E,
    N.translate = E,
    N.exists = m,
    N.detectLanguage = C.detectLanguage,
    N.pluralExtensions = V,
    N.sync = g,
    N.functions = C,
    N.lng = function() {
        return f
    }
    ,
    N.dir = function() {
        return ["ar", "shu", "sqr", "ssh", "xaa", "yhd", "yud", "aao", "abh", "abv", "acm", "acq", "acw", "acx", "acy", "adf", "ads", "aeb", "aec", "afb", "ajp", "apc", "apd", "arb", "arq", "ars", "ary", "arz", "auz", "avl", "ayh", "ayl", "ayn", "ayp", "bbz", "pga", "he", "iw", "ps", "pbt", "pbu", "pst", "prp", "prd", "ur", "ydd", "yds", "yih", "ji", "yi", "hbo", "men", "xmn", "fa", "jpr", "peo", "pes", "prs", "dv", "sam"].some(function(e) {
            return new RegExp("^" + e).test(f)
        }) ? "rtl" : "ltr"
    }
    ,
    N.addPostProcessor = l,
    N.applyReplacement = C.applyReplacement,
    N.options = T,
    N.noConflict = function() {
        window.i18next = window.i18n,
        n ? window.i18n = n : delete window.i18n
    }
}("undefined" == typeof exports ? window : exports);

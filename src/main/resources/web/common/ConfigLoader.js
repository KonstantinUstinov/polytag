function Promise(http) {
    var _handler, _data;

    this.resolve = function(data) {
        if (_handler) {
           _handler(data);
        } else {
            _data = data;
        }
    };

    this.then = function(handler) {
        _handler = handler;
        if (_data) {
            handler(_data);
        }
    };

    this.thenHttpGet = function(configPath, uri, successCallback, errorCallback) {
        this.then(function(conf) {
            var prefix = conf.lookup(configPath);
            http.get(prefix + uri).success(successCallback).error(errorCallback);
        });
    };

    this.thenHttpPut = function(configPath, uri, data, successCallback, errorCallback) {
        this.then(function(conf) {
            var prefix = conf.lookup(configPath);
            http.put(prefix + uri, data).success(successCallback).error(errorCallback);
        });
    };
}

function ConfigLoader(http, location) {

    var configuration, resolvePath, reduceFnc, getCookies, prepareConfig;

    reduceFnc = function(prev, current) {
        return prev[current];
    };

    resolvePath = function(path, config) {
        var tokens = path.split('.');
        return tokens.reduce( reduceFnc, config);
    };

    prepareConfig = function(cfg, urlParams) {
        cfg.lookup = function (path) {
            return resolvePath(path, cfg, location)
        };

        for (var key in urlParams) {
            var tokens = key.split('.');
            tokens.reduce(function (prev, current, i, arr) {
                if (i == arr.length - 1) {
                    prev[current] = urlParams[key];
                } else {
                    if (!prev[current]) {
                        prev[current] = {};
                    }
                    return prev[current];
                }
            }, cfg);
        }
    };

   /* getCookies = function() {
        var cookies = document.cookie;
        var tokens = cookies.split(';').map(function(t) {return t.trim();});
        var env = tokens.reduce(function(prev, current) {
            var kvp = current.split('=');
            prev[kvp[0]] = kvp[1];
            return prev;
        }, {});
        return env;
    };*/

    return function() {

        var promise = new Promise(http);

        if (!configuration) {
            var urlParams = location.search();
            var env = urlParams["env"];

            if (!env) {
                configuration = GLOBAL_ENV_CONFIG;
                prepareConfig(configuration, urlParams);
                promise.resolve(configuration);
                console.log("Loaded default configuration");
            } else {

                http.get("/ui/config/config_portal_" + env + ".json").success(
                    function (data) {
                        configuration = data;
                        prepareConfig(configuration, urlParams);
                        console.log(configuration);
                        promise.resolve(configuration);
                    }
                )
            }

        } else {
            promise.resolve(configuration);
        }

        return promise;
    }
}

function CodeEditor($scope, $http, $q, configLoader, auth){

    // model
    $scope.selectedLanguage = "BeanShell";
    $scope.langMap = {python: 'Python', bsh: "BeanShell", groovy: "Groovy", javascript: "JavaScript"};
    $scope.saveAvailable = false;
    $scope.snippet = undefined;
    $scope.querySource = {selected: null};
    $scope.queryCode = "";
    $scope.entityCode = "";
    $scope.resources = [];
    $scope.showInvisibles = false;

    // view state
    $scope.isQueryCollapsed = false;
    $scope.isEntityCollapsed = false;
    $scope.isHelpCollapsed = true;
    $scope.config = undefined;

    $scope.isFirstWatchFireing = true;

    $scope.$on("downOpenResource", function(ev, message){
        $scope.alerts = [];
        $scope.snippet = message;
        $scope.queryCode = message.query;
        $scope.entityCode = message.code;
        $scope.querySource.selected = undefined;
        $scope.saveAvailable = false;
        $scope.selectedLanguage = $scope.langMap[message.lang] || "BeanShell";
        $scope.refreshResources();
    });

    var defineLanguage = function(){
        var lang = ($scope.selectedLanguage || "beanshell").toLowerCase();
        if (lang === 'beanshell') {
            lang = 'java'
        }
        return lang
    };

    var itemUrl = function(item){
        var v = item.version > 0 ? "v/" + item.version + "/" : "";
        return "/bespoke/" + v + item.path;
    };

    $scope.isSaveEnabled = function(){
        return (!!$scope.querySource.selected) && $scope.saveAvailable;
    };

    $scope.isReleaseEnabled = function(){
        return !$scope.saveAvailable && (!!$scope.querySource.selected);
    };

    $scope.refreshResources = function(){
        $scope.resources = [];
        var bespokeHost = "https://" + window.location.host;
        var cloudApiHost = $scope.config.lookup("service-mapping.cloud-api");
        var ssRestHost = $scope.config.lookup("service-mapping.ss-rest-api");

        var tokenHeader = {'Authorization': "Bearer " + auth.getStoredAuthorizationToken()};

        var headersCR = cloudApiHost.lastIndexOf("https", 0) === 0 ? tokenHeader : {};

        var headersSS = ssRestHost.lastIndexOf("https", 0) === 0 ? tokenHeader : {};

        var promiseCloudApi = $http({ method: 'GET',
            headers: headersCR,
            url: cloudApiHost + "/api/"
        });

        var promiseBespoke = $http({method: 'GET', withCredentials: true, url: bespokeHost + "/resources/releases"});

        var promiseSSRest = $http({ method: 'GET',
            headers: headersSS,
            url: ssRestHost + "/api/"
        });

        var logError = function(data){
            console.log(data);

            if (data.status == 401) {
                $scope.addDangerAlert(data.statusText);
            } else if (data.status > 0) {
                $scope.addDangerAlert(data.status + " : " + data.statusText)
            }
            else {
                $scope.addDangerAlert("Connection to the api host is not available or certificate error, try to open the following link in a new browser tab, accept the SSL certificate and then refresh this page: " + data.config.url)
            }
        };

        var updateSelected = function() {
            $scope.querySource.selected = _.findWhere($scope.resources, {path: $scope.snippet.queryHost});
        };

        promiseCloudApi.then(function(response){
            $scope.resources = _.union($scope.resources, _.map(response.data.apis, function(item){
                return {
                    path: "cloud-api" + item.path,
                    description: _.findWhere(item.operations, {"method": "GET"}).summary
                }
            }));
            updateSelected();
        }).catch(logError);

        promiseBespoke.then(function(response){
            $scope.resources = _.union($scope.resources, _.map(response.data.results, function(item){
                return {
                    path: itemUrl(item),
                    description: item.description
                }
            }));
            updateSelected();
        }).catch(logError);

        promiseSSRest.then(function(response){
            $scope.resources = _.union($scope.resources, _.map(response.data.apis, function(item){
                return {
                    path: "ss-api" + item.path,
                    description: _.findWhere(item.operations, {"method": "GET"}).summary
                }
            }));
            updateSelected();
        }).catch(logError);

    };

    configLoader().then(function(cfg){
        $scope.config = cfg;
    });

    $scope.aceConfig = "{onLoad:aceLoaded, onChange: aceChanged}";

    $scope.aceLoaded = function(_editor){
        _editor.setTheme("ace/theme/crimson_editor");
        _editor.getSession().setMode('ace/mode/' + defineLanguage());
        _editor.setShowPrintMargin(false);
        _editor.getSession().setUseSoftTabs(true);
        _editor.setOption("showInvisibles", $scope.showInvisibles);
        _editor.getSession().on("change", function(editor){
            var maxLen = parseInt($scope.config.lookup("editor-max-length"));
            var doc = _editor.getSession().getDocument();
            if ( doc.getValue().length > maxLen )
            {
                var carpos = _editor.getCursorPosition();
                var text = doc.getValue().substr(0, maxLen);
                _editor.getSession().setValue(text);
                _editor.moveCursorToPosition(carpos);
            }
        });
    };

    $scope.aceChanged = function(e){
        $scope.saveAvailable = true;
    };

    $scope.resizeEditor = function(editorId){
        $(editorId).resize();
    };

    $scope.$watch('selectedLanguage', function(newValue, oldValue){

        if (newValue == oldValue) {
            return;
        }

        var lang = defineLanguage();
        if (!$scope.isFirstWatchFireing) $scope.saveAvailable = true;
        $scope.isFirstWatchFireing = false;

        $('.codeEditor').each(function(index){
            ace.edit(this).getSession().setMode('ace/mode/' + lang);
        });
    });

    $scope.toggleInvisibles = function(){
        $scope.showInvisibles = !$scope.showInvisibles;
        $('.codeEditor').each(function(index){
            ace.edit(this).setOption("showInvisibles", $scope.showInvisibles)
        });
    };

    $scope.querySourceChanged = function(){
        $scope.saveAvailable = true;
    };

    $scope.onSave = function(isSave){
        $scope.alerts = [];

        var lng = $scope.selectedLanguage.toLowerCase();
        lng = lng === 'beanshell' ? 'bsh' : lng;

        var url = "https://" + window.location.host +
            "/resources/snippet/" + $scope.snippet.path;

        var code = {
            "queryCode": $scope.queryCode,
            "entityCode": $scope.entityCode,
            "description": $scope.snippet.description,
            "queryHost": $scope.querySource.selected.path,
            "lang": lng
        };

        if (isSave) {
            $http({method: 'PUT', withCredentials: true, url: url, data: JSON.stringify(code)}).success(function(){
                $scope.saveAvailable = false;
                $scope.addSuccessAlert("The code was saved successfully");
                $scope.$emit("upReloadSnippets");

            }).error(function(data, status, headers, config){
                $scope.addDangerAlert(data.code + " : " + data.message)
            })
        } else {
            $http({method: 'POST', withCredentials: true, url: url, data: JSON.stringify(code)}).success(function(){
                $scope.addSuccessAlert("The new version was released successfully");
                $scope.$emit("upReloadSnippets");

            }).error(function(data, status, headers, config){
                $scope.addDangerAlert(data.code + " : " + data.message)
            })
        }
    };

    $scope.alerts = [];

    $scope.addDangerAlert = function(msg){
        $scope.alerts.push({type: "danger", msg: msg});
    };

    $scope.addSuccessAlert = function(msg){
        $scope.alerts.push({type: "success", msg: msg});
    };

    $scope.closeAlert = function(index){
        $scope.alerts.splice(index, 1);
    };
}

function ExecuteRequest($scope, $http) {

    $scope.defaultData = "search={}&limit=100&metrics";
    $scope.data = $scope.defaultData;
    $scope.result = "";
    $scope.isEncoded = false;
    $scope.snippet = undefined;
    $scope.processing = false;

    $scope.$on("downOpenResource", function(ev, message) {
        $scope.snippet = message;
    });

    $scope.aceLoaded = function(ace) {
        ace.setTheme("ace/theme/crimson_editor");
        ace.getSession().setMode("ace/mode/javascript");
        ace.renderer.setShowGutter(false);
        ace.setShowPrintMargin(false);
        ace.getSession().setUseWrapMode(true);
        ace.setHighlightActiveLine(false);
        ace.getSession().setUseSoftTabs(true);
    };

    $scope.resizeEditor = function() {
        $("#editor").resize();
    };

    $scope.setDefault = function() {
        $scope.data = $scope.defaultData;
    };

    $scope.encode = function() {
        $scope.data = encodeURI($scope.data)
    };

    $scope.decode = function() {
        $scope.data = decodeURI($scope.data)
    };

    $scope.execute = function() {
        $scope.result = "";
        $scope.processing = true;

        var params = $scope.data;
        if (!$scope.isEncoded) {
            params = encodeURI(params);
        }

        var url = "https://" + window.location.host +
            "/bespoke/v/" + $scope.snippet.version +
            "/" + $scope.snippet.path +
            "?" + params;

        $scope.result = "Executing request: " + url + "\n";

        $http({
            method: 'GET',
            withCredentials: true,
            url: url,
            data: "",
            headers: {
                "X-WRITE_OPLOG": "true",
                "X-UI-Initiated": "true"
            }
        }).success(function(data) {
            $scope.result += JSON.stringify(data, undefined, 4);
            $scope.processing = false;
        }).error(function(data, status) {
            var msg = "";
            if (data != null) {
                msg = data.message != null ? data.message : data.toString();
            } else {
                msg = "No connection or authorization error"
            }
            $scope.result += "Error " + status + ": " + msg.replace("\\n", "\n").replace("\\t", "\t");
            $scope.processing = false;
        })
    }
}

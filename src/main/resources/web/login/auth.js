function auth(base64, $http){
    return {

        Login: function(username, password, state, redirectUri, client_id, client_secret, onSuccess, onError){

            var req = {
                method: 'POST',
                url: '/login?client_id=' + client_id +
                "&client_secret="+ client_secret +
                (state ? '&state=' + state : "") +
                "&redirect_uri=" + redirectUri +
                "&response_type=code",

                headers: {
                    Authorization: 'Basic ' + base64.encode(username + ":" + password)
                }
            };

            $http(req)
                .success(function(data, status, headers, config){
                    onSuccess(data)
                }).error(function(data, status, headers, config){
                    var msg = "";
                    if (status == 0) msg = "Connection to the authentication server failed (host not responding or wrong SSL certificate)";
                    else if (status == 401) msg = "Authentication has failed";
                    else msg = data;

                    onError({error: msg})
                });
        }

    }
}

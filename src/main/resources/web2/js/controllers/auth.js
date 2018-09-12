function auth($cookies){
    return {
        clearAuthorizationToken: function(){
            $.removeCookie("Authorization", { path: '/' });
        },
        getStoredAuthorizationToken: function(){
            var h = $cookies.Authorization;
            if (!!h && h.indexOf("Bearer ") == 0) {
                return h.substring("Bearer ".length);
            } else {
                return null
            }
        }
    }
}


function CommonUtilites() {
    return {

        toDateStringFormatted : function(milliseconds) {
            var dt = new Date(milliseconds);
            return (dt.getMonth() + 1) + "." + dt.getDate() + "." + dt.getFullYear() + " " + dt.getHours() + ":" + dt.getMinutes();
        },

        lastChars: function(str, n) {
            if ( str.length <= n ) {
                return str;
            }

            return '...' + str.substr(str.length - n);
        }
    }
}

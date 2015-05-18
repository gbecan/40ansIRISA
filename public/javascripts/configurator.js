/**
 * Created by gbecan on 05/11/14.
 */

var brefApp = angular.module('brefApp', []);

brefApp.controller('ConfiguratorCtrl', function ($scope, $http) {

    $scope.activated = false;

    $scope.hasFlash = false;
    var plugins = navigator.plugins;
    for (var i = 0; i < plugins.length; i++) {
        if (plugins[i].description.toLowerCase().indexOf("flash") > -1) {
            $scope.hasFlash = true;
        }
    }

    $scope.generate = function() {
        $scope.activated = true;


        var playlistURL = "get-playlist";


        if ($scope.hasFlash) { // Flash

            console.log("flash player");
            flowplayer("player", "assets/flash/flowplayer.swf", {
                wmode: 'direct',
                plugins: {
                    httpstreaming: {
                        url: 'assets/flash/flashlsFlowPlayer.swf',
                        hls_debug : false,
                        hls_debug2 : false,
                        hls_lowbufferlength : 3,
                        hls_minbufferlength : 5,
                        hls_maxbufferlength : 0,
                        hls_startfromlevel : -1,
                        hls_seekfromlevel : -1,
                        hls_live_flushurlcache : false,
                        hls_seekmode : "ACCURATE",
                        hls_fragmentloadmaxretry : -1,
                        hls_manifestloadmaxretry : -1,
                        hls_capleveltostage : false,
                        hls_maxlevelcappingmode : "downscale"
                    }
                },
                clip: {
                    accelerated: true,
                    url: playlistURL,
                    urlResolvers: "httpstreaming",
                    lang: "fr",
                    provider: "httpstreaming",
                    autoPlay: false,
                    autoBuffering: true
                },
                log: {
                    level: 'debug',
                    filter: 'org.flowplayer.controller.*'
                }
            });

        } else { // HTML5
            console.log("Error: Flash not supported");
        }
    };
});
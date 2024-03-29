var irisaApp = angular.module('irisaApp', []);

irisaApp.controller('ConfiguratorCtrl', function ($scope, $http) {

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


        $http.get("/generate-playlist").success(function (data) {

            $scope.counter = data.counter;
            var playlistURL = data.playlistURL;

            $scope.play(playlistURL)

        });

    };

    $scope.play = function (playlistURL) {
        if ($scope.hasFlash) { // Flash

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
                    level: 'none',
                    filter: 'org.flowplayer.controller.*'
                }
            });

        } else { // HTML5
            console.log("Error: Flash not supported");
        }
    };

    new Konami(function() {
        $scope.activated = true;
        $scope.$apply();
        $scope.play("get-playlist?vp0=0");
    });
});
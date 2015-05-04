/**
 * Created by gbecan on 05/11/14.
 */

var brefApp = angular.module('brefApp', []);

brefApp.controller('ConfiguratorCtrl', function ($scope, $http) {

    $scope.activated = false;

    $scope.vps = [{
        options : [
            {
                poster : ""
            }
        ]
    }];
    $http.get("getvps").success(function(data) {
        $scope.vps = data;
    });

    $scope.hasFlash = false;
    var plugins = navigator.plugins;
    for (var i = 0; i < plugins.length; i++) {
        if (plugins[i].description.toLowerCase().indexOf("flash") > -1) {
            $scope.hasFlash = true
        }
    }



    //$scope.hasFlash = false; // FIXME
    $scope.hasFlash = true // FIXME

    $scope.generate = function() {
        $scope.activated = true;

        var parameters = "";
        for (var i = 0; i < $scope.vps.length; i++) {
            var selectedOption = $scope.vps[i].selected;
            if (i > 0) {
                parameters += "&"
            }
            var vpid = i + 1;
            parameters += "vp" + vpid + "=" + selectedOption.trim()
        }

        var playlistURL = "get-playlist?" + parameters;

        console.log(playlistURL);

        console.log("flash? = " + $scope.hasFlash);

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
            console.log("HTML5 player");
            $http.get("get-playlist-html5?" + parameters).success(function(data) {

                var videos = data;

                var videoPlayer = document.getElementById('videoPlayer');

                window.MediaSource = window.MediaSource || window.WebKitMediaSource;

                var mediaSource = new MediaSource();

                var video = document.getElementById('videoPlayer');
                video.src = window.URL.createObjectURL(mediaSource);

                mediaSource.addEventListener('sourceopen', function(e) {
                    var sourceBuffer = mediaSource.addSourceBuffer('video/webm; codecs="vorbis,vp8"');
                    console.log(sourceBuffer);
                    sourceBuffer.appendBuffer(new Uint8Array(videos[0]));
                    console.log(sourceBuffer);
                    video.play();
                    console.log(sourceBuffer);
                    sourceBuffer.addEventListener('updateend', function(e) {
                        mediaSource.endOfStream();
                    });
                    //for (var i = 0; i < videos.length; i++) {
                    //    console.log(videos[i]);
                    //    //if (i == videos.length - 1 ) {
                    //    //    mediasource.endOfStream();
                    //    //}
                    //    //sourceBuffer.append(videos[i])
                    //    sourceBuffer.appendBuffer(new Uint8Array(videos[i]))
                    //}
                    //start playing after video header recieved(i.e; first chunk)
                    //call media.endOfStream(); before appending last chunk
                }, false);



//            var i = 1;
//            videoPlayer.onended = function(){
//                videoPlayer.src = BrefVideo[i];
//                i++;
//            }
//                videoPlayer.src = BrefVideo[0];
            });
        }
    };
});
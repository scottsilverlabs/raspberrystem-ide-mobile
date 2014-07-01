var target = "";
var search;

function apploaded() {
    console.log("Loaded");
    search();
}

function redirect(url) {
    window.location = "http://"+url;
}

function search() {
    var ip = "";
    var found = false;
    var number = 0;
    function test(url) {  
        var holder = document.getElementById("links");
        var req = new XMLHttpRequest();
        var first = true;
        req.onload = function (e) {
            if (req.readyState === 4) {
                if (req.status === 200) {
                    found = true;                 
                    holder.innerHTML += "<div id=\"linkHolder\"><a href=\"http://"+url+"\"><b>"+req.responseText+"</b>: "+url+"</a><br></div>";
                }
                if (++number == 250) {
                    document.getElementById("title").innerHTML = "Loaded";
                }
                console.log(number);
            }
        };
        req.onerror = function (e) {
            if (++number == 250) {
                document.getElementById("title").innerHTML = "Loaded";
                if (!found) {
                    holder.innerHTML += "<div id=\"linkHolder\"><b>No Servers Found</b>: <input type=\"text\" class=\"ip\" placeholder=\"IP Address\" onchange=\"redirect(this.value)\"><br></div>";
                }
            }
            console.log(number);
        };
        req.open("GET", "http://"+url+"/api/hostname", true);
        req.send(null);
    }

    var RTCPeerConnection = /*window.RTCPeerConnection ||*/ window.webkitRTCPeerConnection || window.mozRTCPeerConnection;
    if (RTCPeerConnection) (function () {
        var rtc = new RTCPeerConnection({iceServers:[]});
        if (window.mozRTCPeerConnection) {// FF needs a channel/stream to proceed
            rtc.createDataChannel('', {reliable:false});
        };
        
        rtc.onicecandidate = function (evt) {
            if (evt.candidate) grepSDP(evt.candidate.candidate);
        };
        rtc.createOffer(function (offerDesc) {
            grepSDP(offerDesc.sdp);
            rtc.setLocalDescription(offerDesc);
        }, function (e) { console.warn("offer failed", e); });

        function grepSDP(sdp) {
            var ips = sdp.match(/\d+\.\d+\.\d+\.\d+/);
            if (ips[0] == "127.0.0.1" || ip != "") {
                return;
            }
            ip = ips[0];
            if (ip === "") {
                ip = "192.168.1.1";
            }
            ip = ip.match(/\d+\.\d+\.\d+/)[0];
            for (var i = 2; i < 255; i++) {
                test(ip+"."+i.toString());
            }
        }
    })(); else {
        ip = "192.168.1";
        for (var i = 2; i < 255; i++) {
            test(ip+"."+i.toString());
        }
    }
}
// Polygon getBounds extension - google-maps-extensions
if (!google.maps.Polygon.prototype.getBounds) {
    google.maps.Polygon.prototype.getBounds = function(latLng) {
        var bounds = new google.maps.LatLngBounds(),
            paths = this.getPaths(),
            path,
            p, i;

        for (p = 0; p < paths.getLength(); p++) {
            path = paths.getAt(p);
            for (i = 0; i < path.getLength(); i++) {
                bounds.extend(path.getAt(i));
            }
        }

        return bounds;
    };
}

// Polygon containsLatLng - method to determine if a latLng is within a polygon
google.maps.Polygon.prototype.containsLatLng = function(latLng) {
    // Exclude points outside of bounds as there is no way they are in the poly

    var inPoly = false,
        bounds, lat, lng,
        numPaths, p, path, numPoints,
        i, j, vertex1, vertex2;

    // Arguments are a pair of lat, lng variables
    if (arguments.length == 2) {
        if (
            typeof arguments[0] == "number" &&
            typeof arguments[1] == "number"
        ) {
            lat = arguments[0];
            lng = arguments[1];
        }
    } else if (arguments.length == 1) {
        bounds = this.getBounds();

        if (!bounds && !bounds.contains(latLng)) {
            return false;
        }
        lat = latLng.lat();
        lng = latLng.lng();
    } else {
        console.log("Wrong number of inputs in google.maps.Polygon.prototype.contains.LatLng");
    }

    // Raycast point in polygon method

    numPaths = this.getPaths().getLength();
    for (p = 0; p < numPaths; p++) {
        path = this.getPaths().getAt(p);
        numPoints = path.getLength();
        j = numPoints - 1;

        for (i = 0; i < numPoints; i++) {
            vertex1 = path.getAt(i);
            vertex2 = path.getAt(j);

            if (
                vertex1.lng() <  lng &&
                vertex2.lng() >= lng ||
                vertex2.lng() <  lng &&
                vertex1.lng() >= lng
            ) {
                if (
                    vertex1.lat() +
                    (lng - vertex1.lng()) /
                    (vertex2.lng() - vertex1.lng()) *
                    (vertex2.lat() - vertex1.lat()) <
                    lat
                ) {
                    inPoly = !inPoly;
                }
            }

            j = i;
        }
    }

    return inPoly;
};


function findDuplicates(arr) {
    var len=arr.length,
        out=[],
        counts={};

    for (var i=0;i<len;i++) {
        var item = arr[i];
        counts[item] = counts[item] >= 1 ? counts[item] + 1 : 1;
    }

    for (var item in counts) {
        if(counts[item] > 1)
            out.push(item);
    }

    return out;
}

function calculateDistance(originAddress, destinationAddress){
    var service = new google.maps.DistanceMatrixService;
    service.getDistanceMatrix({
        origins: [originAddress],
        destinations: [destinationAddress],
        travelMode: google.maps.TravelMode.DRIVING,
        unitSystem: google.maps.UnitSystem.METRIC,
        avoidHighways: false,
        avoidTolls: false
    }, function(response, status) {
        if (status !== google.maps.DistanceMatrixStatus.OK) {
            alert('Error was: ' + status);
        } else {
            var originList = response.originAddresses;
            var destinationList = response.destinationAddresses;

            for (var i = 0; i < originList.length; i++) {
                var results = response.rows[i].elements;

                for (var j = 0; j < results.length; j++) {
                    $("#distanceBetweenOffices").val(results[j].distance.text);
                    $("#tripDurationBetweenOffices").val(results[j].duration.text);
                }
            }
        }
    });
}

function fncRouteZoneIntersection(response) {

    var myRoute = response.routes[0].overview_path;
    var lngLatCordinates = new Array();

    for (var i = 0; i < myRoute.length; i++) {
        lngLatCordinates.push(myRoute[i]);
    }
    return lngLatCordinates;
}

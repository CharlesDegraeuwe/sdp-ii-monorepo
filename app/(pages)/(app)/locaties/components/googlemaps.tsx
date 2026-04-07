'use client';
import React from 'react';
import { GoogleMap, useJsApiLoader } from '@react-google-maps/api';

const containerStyle = {
  width: '100%',
  height: '100%',
  maxHeight: '100%',
  maxWidth: '100%',
  featureType: 'all',
  elementType: 'labels',
  stylers: [{ visibility: 'off' }],
};

const center = { lat: 50.503, lng: 4.47 };

function GoogleMaps() {
  const { isLoaded } = useJsApiLoader({
    id: 'google-map-script',
    googleMapsApiKey: process.env.NEXT_PUBLIC_GOOGLE_API_KEY!,
  });

  const [map, setMap] = React.useState(null);

  const onLoad = React.useCallback(function callback(map) {
    // This is just an example of getting and using the map instance!!! don't just blindly copy!
    const bounds = new window.google.maps.LatLngBounds(center);
    map.fitBounds(bounds);

    setMap(map);
  }, []);

  const onUnmount = React.useCallback(function callback(map) {
    setMap(null);
  }, []);

  return isLoaded ? (
    <div className={'w-full h-full flex z-0'}>
      <GoogleMap
        mapContainerStyle={containerStyle}
        center={center}
        zoom={8}
        options={{
          minZoom: 2,
          mapTypeId: 'satellite',
          mapTypeControl: false,
          fullscreenControl: false,
          streetViewControl: false,
          cameraControl: false,
          keyboardControl: false,

          restriction: {
            latLngBounds: {
              north: 52,
              south: 49,
              east: 7,
              west: 2,
            },
            strictBounds: true,
          },
          styles: [
            // Alles standaard labels uit
            {
              featureType: 'all',
              elementType: 'labels',
              stylers: [{ visibility: 'off' }],
            },
            // Landgrenzen aan
            {
              featureType: 'administrative.country',
              elementType: 'geometry.stroke',
              stylers: [{ visibility: 'on' }, { weight: 2 }],
            },
            // Provinciegrenzen (subtiel)
            {
              featureType: 'administrative.province',
              elementType: 'geometry.stroke',
              stylers: [{ visibility: 'on' }, { weight: 1 }],
            },
            // Landnamen aan
            {
              featureType: 'administrative.country',
              elementType: 'labels',
              stylers: [{ visibility: 'on' }],
            },
            // Alleen grote steden (locality = steden/gemeenten)
            {
              featureType: 'administrative.locality',
              elementType: 'labels',
              stylers: [{ visibility: 'on' }],
            },
            // Wijken, buurten, dorpen weg
            {
              featureType: 'administrative.neighborhood',
              elementType: 'labels',
              stylers: [{ visibility: 'off' }],
            },
            // POI's (restaurants, winkels, etc.) weg
            {
              featureType: 'poi',
              elementType: 'labels',
              stylers: [{ visibility: 'off' }],
            },
            // Straatnamen weg
            {
              featureType: 'road',
              elementType: 'labels',
              stylers: [{ visibility: 'off' }],
            },
            // Transitlabels (stations etc.) weg
            {
              featureType: 'transit',
              elementType: 'labels',
              stylers: [{ visibility: 'off' }],
            },
            // Waternamen weg
            {
              featureType: 'water',
              elementType: 'labels',
              stylers: [{ visibility: 'off' }],
            },
          ],
        }}
      >
        {/* Child components, such as markers, info windows, etc. */}
        <></>
      </GoogleMap>
    </div>
  ) : (
    <></>
  );
}

export default GoogleMaps;

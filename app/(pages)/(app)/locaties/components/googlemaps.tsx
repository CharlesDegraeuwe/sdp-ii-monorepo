'use client';
import React from 'react';
import { GoogleMap, useJsApiLoader } from '@react-google-maps/api';

const containerStyle = {
  width: '100%',
  height: '100%',
  maxHeight: '100%',
  maxWidth: '100%',
};

const center = { lat: 50.503, lng: 4.47 };

function GoogleMaps() {
  const { isLoaded } = useJsApiLoader({
    id: 'google-map-script',
    googleMapsApiKey: process.env.NEXT_PUBLIC_GOOGLE_API_KEY!,
  });

  const onLoad = React.useCallback(() => {
    // map loaded
  }, []);

  const onUnmount = React.useCallback(() => {
    // map unmounted
  }, []);

  return isLoaded ? (
    <div className="w-full h-full flex z-0">
      <GoogleMap
        mapContainerStyle={containerStyle}
        center={center}
        zoom={8}
        onLoad={onLoad}
        onUnmount={onUnmount}
        options={{
          minZoom: 2,
          mapTypeId: 'satellite',
          mapTypeControl: false,
          fullscreenControl: false,
          streetViewControl: false,
          cameraControl: false,
          keyboardShortcuts: false,
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
            {
              featureType: 'all',
              elementType: 'labels',
              stylers: [{ visibility: 'off' }],
            },
            {
              featureType: 'administrative.country',
              elementType: 'geometry.stroke',
              stylers: [{ visibility: 'on' }, { weight: 2 }],
            },
            {
              featureType: 'administrative.province',
              elementType: 'geometry.stroke',
              stylers: [{ visibility: 'on' }, { weight: 1 }],
            },
            {
              featureType: 'administrative.country',
              elementType: 'labels',
              stylers: [{ visibility: 'on' }],
            },
            {
              featureType: 'administrative.locality',
              elementType: 'labels',
              stylers: [{ visibility: 'on' }],
            },
            {
              featureType: 'administrative.neighborhood',
              elementType: 'labels',
              stylers: [{ visibility: 'off' }],
            },
            {
              featureType: 'poi',
              elementType: 'labels',
              stylers: [{ visibility: 'off' }],
            },
            {
              featureType: 'road',
              elementType: 'labels',
              stylers: [{ visibility: 'off' }],
            },
            {
              featureType: 'transit',
              elementType: 'labels',
              stylers: [{ visibility: 'off' }],
            },
            {
              featureType: 'water',
              elementType: 'labels',
              stylers: [{ visibility: 'off' }],
            },
          ],
        }}
      >
        <></>
      </GoogleMap>
    </div>
  ) : (
    <></>
  );
}

export default GoogleMaps;

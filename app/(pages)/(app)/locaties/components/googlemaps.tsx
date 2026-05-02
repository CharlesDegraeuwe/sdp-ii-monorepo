'use client';

import React, { useState, useEffect, useRef } from 'react';
import { GoogleMap, Marker, useJsApiLoader } from '@react-google-maps/api';
import { Site } from '@/types/types';

const containerStyle = {
  width: '100%',
  height: '100%',
  maxHeight: '100%',
  maxWidth: '100%',
};

const center = { lat: 50.503, lng: 4.47 };

export interface MapSite {
  id: number;
  naam: string;
  locatie: string;
  capaciteit: number;
  status: string;
  machines?: unknown[];
  teams?: unknown[];
}

export interface MarkerData extends MapSite {
  position: google.maps.LatLng | google.maps.LatLngLiteral;
}

interface GoogleMapsProps {
  selectedSite: Site | null;
  sites?: MapSite[];
  onMarkerClick?: (site: MapSite) => void;
}

export default function GoogleMaps(props: GoogleMapsProps) {
  const { selectedSite, sites = [], onMarkerClick } = props;
  const { isLoaded } = useJsApiLoader({
    id: 'google-map-script',
    googleMapsApiKey: process.env.NEXT_PUBLIC_GOOGLE_API_KEY!,
  });

  const [markers, setMarkers] = useState<MarkerData[]>([]);
  const mapRef = useRef<google.maps.Map | null>(null);

  useEffect(() => {
    if (!isLoaded || sites.length === 0) return;

    const geocoder = new window.google.maps.Geocoder();

    const fetchCoordinates = async () => {
      const resolvedMarkers = await Promise.all(
        sites.map((site) => {
          return new Promise<MarkerData | null>((resolve) => {
            geocoder.geocode({ address: site.locatie }, (results, status) => {
              if (status === 'OK' && results && results[0]) {
                resolve({ ...site, position: results[0].geometry.location });
              } else {
                console.error(site.locatie);
                resolve(null);
              }
            });
          });
        }),
      );
      setMarkers(resolvedMarkers.filter((m): m is MarkerData => m !== null));
    };

    fetchCoordinates();
  }, [isLoaded, sites]);

  useEffect(() => {
    if (!mapRef.current) return;

    if (selectedSite) {
      const marker = markers.find((m) => m.id === selectedSite.id);
      if (marker) {
        mapRef.current.panTo(marker.position);
        mapRef.current.setZoom(13);
      }
    } else {
      mapRef.current.panTo(center);
      mapRef.current.setZoom(8);
    }
  }, [selectedSite, markers]);

  const getCustomPin = (status: string, naam: string) => {
    const color =
      status === 'Actief'
        ? '#34d399'
        : status === 'In onderhoud'
          ? '#fbbf24'
          : '#fb7185';

    const letter = naam ? naam.charAt(0).toUpperCase() : '?';

    const svg = `
      <svg width="52" height="64" viewBox="0 0 52 64" fill="none" xmlns="http://www.w3.org/2000/svg">
        <g transform="translate(2, 2)">
          <path d="M24 0C10.745 0 0 10.745 0 24c0 18 24 36 24 36s24-18 24-36C48 10.745 37.255 0 24 0z" 
                fill="${color}" fill-opacity="0.85" 
                stroke="#111827" stroke-width="2"/>
          <text x="24" y="32" font-family="Arial, sans-serif" font-size="22" font-weight="bold" fill="white" text-anchor="middle">${letter}</text>
        </g>
      </svg>
    `;

    return `data:image/svg+xml;charset=UTF-8,${encodeURIComponent(svg)}`;
  };

  return isLoaded ? (
    <div className="w-full h-full flex z-0">
      <GoogleMap
        mapContainerStyle={containerStyle}
        center={center}
        zoom={8}
        onLoad={(map) => {
          mapRef.current = map;
        }}
        onUnmount={() => {
          mapRef.current = null;
        }}
        options={{
          minZoom: 2,
          mapTypeId: 'roadmap',
          mapTypeControl: false,
          fullscreenControl: false,
          streetViewControl: false,
          cameraControl: false,
          keyboardShortcuts: false,
          restriction: {
            latLngBounds: { north: 52, south: 49, east: 7, west: 2 },
            strictBounds: true,
          },
          styles: [
            {
              featureType: 'all',
              stylers: [{ saturation: -100 }, { lightness: 15 }],
            },
            {
              featureType: 'all',
              elementType: 'labels',
              stylers: [{ visibility: 'off' }],
            },
            {
              featureType: 'landscape',
              elementType: 'geometry',
              stylers: [{ color: '#f5f5f5' }],
            },
            {
              featureType: 'water',
              elementType: 'geometry',
              stylers: [{ color: '#d3d3d3' }],
            },
            {
              featureType: 'road.highway',
              elementType: 'geometry.fill',
              stylers: [{ color: '#ffffff' }],
            },
            {
              featureType: 'road.highway',
              elementType: 'geometry.stroke',
              stylers: [{ color: '#e0e0e0' }],
            },
            {
              featureType: 'road.local',
              elementType: 'geometry',
              stylers: [{ color: '#ffffff' }, { weight: 0.5 }],
            },
            {
              featureType: 'poi.park',
              elementType: 'geometry',
              stylers: [{ color: '#e5e5e5' }],
            },
            {
              featureType: 'administrative.country',
              elementType: 'geometry.stroke',
              stylers: [
                { visibility: 'on' },
                { color: '#808080' },
                { weight: 1.5 },
              ],
            },
          ],
        }}
      >
        {markers.map((marker, index) => (
          <Marker
            key={marker.id || index}
            position={marker.position}
            title={marker.naam}
            icon={{
              url: getCustomPin(marker.status, marker.naam),
              anchor: new window.google.maps.Point(26, 62),
            }}
            onClick={() => onMarkerClick?.(marker)}
          />
        ))}
      </GoogleMap>
    </div>
  ) : (
    <></>
  );
}

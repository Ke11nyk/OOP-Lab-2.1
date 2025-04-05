import { useEffect, useState } from 'react';
import api from '../api/client';
import { Flight } from '../types/types';
import FlightCard from './FlightCard';
import './FlightList.css';

interface FlightListProps {
    departure?: string;
    arrival?: string;
}

const FlightList = ({ departure = '', arrival = '' }: FlightListProps) => {
    const [flights, setFlights] = useState<Flight[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        console.log('FlightList component mounted');
        const fetchFlights = async () => {
            console.log('Starting an API request...');
            try {
                // Створюємо об'єкт параметрів тільки з непустими значеннями
                const params: Record<string, string> = {};

                if (departure.trim()) params.departure = departure.trim();
                if (arrival.trim()) params.arrival= arrival.trim();

                console.log('Request params:', params); // Додаємо лог для перевірки параметрів

                const response = await api.get('/flights', {
                    params: params
                });

                console.log('API response:', response);
                setFlights(response.data);
            } catch (err: any) {
                console.error('Error details:', err.response || err);
                setError('Failed to load flights');
            } finally {
                setLoading(false);
            }
        };

        fetchFlights();
    }, [departure, arrival]);

    if (loading) return <div className="loading">Loading flights...</div>;
    if (error) return <div className="error">{error}</div>;
    if (flights.length === 0) return <div>No flights available</div>;

    return (
        <div className="flight-list">
            {flights.map(flight => (
                <FlightCard key={flight.id} flight={flight} />
            ))}
        </div>
    );
};

export default FlightList;
import { useEffect, useState } from 'react';
import api from '../api/client';
import { Flight } from '../types/types';
import FlightCard from './FlightCard';
import './FlightList.css';

const FlightList = () => {
    const [flights, setFlights] = useState<Flight[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        console.log('FlightList component mounted');
        const fetchFlights = async () => {
            console.log('Starting an API request...');
            try {
                const response = await api.get('/flights');
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
    }, []);

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
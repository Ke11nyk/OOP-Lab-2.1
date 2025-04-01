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
        const fetchFlights = async () => {
            try {
                const response = await api.get('/flights');
                setFlights(response.data);
            } catch (err) {
                setError('Failed to load flights');
                console.error(err);
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
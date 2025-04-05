import { useState } from 'react';
import FlightList from '../components/FlightList';
import './FlightsPage.css';

const FlightsPage = () => {
    const [departure, setDeparture] = useState('');
    const [arrival, setArrival] = useState('');

    return (
        <div className="flights-page">
            <h1>Available Flights</h1>
            <div className="flight-search">
                <input
                    type="text"
                    placeholder="Departure city"
                    value={departure}
                    onChange={(e) => setDeparture(e.target.value)}
                />
                <input
                    type="text"
                    placeholder="Arrival city"
                    value={arrival}
                    onChange={(e) => setArrival(e.target.value)}
                />
            </div>
            <FlightList departure={departure} arrival={arrival} />
        </div>
    );
};

export default FlightsPage;
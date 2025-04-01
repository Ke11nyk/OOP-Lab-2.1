import { Flight } from '../types/types';
import BookingForm from './BookingForm';
import './FlightCard.css';

interface FlightCardProps {
    flight: Flight;
}

const FlightCard = ({ flight }: FlightCardProps) => {
    return (
        <div className="flight-card">
            <div className="flight-header">
                <h3>{flight.flightNumber}</h3>
                <span className="price">${flight.currentPrice.toFixed(2)}</span>
            </div>

            <div className="flight-route">
        <span className="departure">
          <strong>{flight.departureAirport}</strong>
          <br />
            {new Date(flight.departureTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
        </span>

                <span className="arrow">→</span>

                <span className="arrival">
          <strong>{flight.arrivalAirport}</strong>
          <br />
                    {new Date(flight.arrivalTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
        </span>
            </div>

            <div className="flight-details">
                <span>Seats available: {flight.availableSeats}</span>
            </div>

            <BookingForm flightId={flight.id} />
        </div>
    );
};

export default FlightCard;
import FlightList from '../components/FlightList';
import './FlightsPage.css';

const FlightsPage = () => {
    return (
        <div className="flights-page">
            <h1>Available Flights</h1>
            <FlightList />
        </div>
    );
};

export default FlightsPage;
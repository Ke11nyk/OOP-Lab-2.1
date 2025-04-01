import { Link } from 'react-router-dom';
import './HomePage.css';

const HomePage = () => {
    return (
        <div className="home-page">
            <h1>Welcome to LowCost Airlines</h1>
            <p>Find the best deals for your next trip</p>
            <Link to="/flights" className="cta-button">Browse Flights</Link>
        </div>
    );
};

export default HomePage;
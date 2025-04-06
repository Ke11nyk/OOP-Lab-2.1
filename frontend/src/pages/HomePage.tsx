import { Link } from 'react-router-dom';
import { useEffect, useState } from 'react';
import api from '../api/client';
import './HomePage.css';

const HomePage = () => {
    const [user, setUser] = useState<{ fullName?: string } | null>(null);
    const isAuthenticated = !!localStorage.getItem('authToken');

    useEffect(() => {
        const fetchUser = async () => {
            if (isAuthenticated) {
                try {
                    const userId = localStorage.getItem('userId');
                    if (userId) {
                        const response = await api.get(`/users?id=${userId}`);
                        setUser(response.data);
                    }
                } catch (error) {
                    console.error('Error fetching user data:', error);
                }
            }
        };

        fetchUser();
    }, [isAuthenticated]);

    return (
        <div className="home-page">
            <h1>Welcome to LowCost Airlines</h1>
            {isAuthenticated && user?.fullName && (
                <div className="user-greeting">
                    <p>Hello, {user.fullName}!</p>
                    <Link to="/profile" className="profile-link">View your profile</Link>
                </div>
            )}
            <p>Find the best deals for your next trip</p>
            <Link to="/flights" className="cta-button">Browse Flights</Link>
        </div>
    );
};

export default HomePage;
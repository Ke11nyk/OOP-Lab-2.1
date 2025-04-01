import { useEffect, useState } from 'react';
import api from '../api/client';
import { Booking, User } from '../types/types';
import './ProfilePage.css';

const ProfilePage = () => {
    const [user, setUser] = useState<User | null>(null);
    const [bookings, setBookings] = useState<Booking[]>([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const userResponse = await api.get('/users/me');
                const bookingsResponse = await api.get('/users/me/bookings');
                setUser(userResponse.data);
                setBookings(bookingsResponse.data);
            } catch (error) {
                console.error('Failed to fetch profile data:', error);
            }
        };

        fetchData();
    }, []);

    if (!user) return <div>Loading profile...</div>;

    return (
        <div className="profile-page">
            <h1>My Profile</h1>
            <div className="profile-info">
                <h2>{user.fullName}</h2>
                <p>{user.email}</p>
            </div>

            <h2>My Bookings</h2>
            {bookings.length === 0 ? (
                <p>No bookings yet</p>
            ) : (
                <ul className="bookings-list">
                    {bookings.map(booking => (
                        <li key={booking.id}>
                            <div>Booking #{booking.bookingNumber}</div>
                            <div>Status: {booking.status}</div>
                            <div>Total: ${booking.totalPrice.toFixed(2)}</div>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default ProfilePage;
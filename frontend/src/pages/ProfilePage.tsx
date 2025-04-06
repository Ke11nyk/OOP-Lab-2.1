import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/client';
import { Booking, User, Flight } from '../types/types';
import './ProfilePage.css';

const ProfilePage = () => {
    const [user, setUser] = useState<User | null>(null);
    const [bookings, setBookings] = useState<(Booking & { flight?: Flight })[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchData = async () => {
            // 1. Отримуємо userId з localStorage
            const userId = localStorage.getItem('userId');

            // 2. Перевіряємо, що userId існує і валідний
            if (!userId || isNaN(parseInt(userId))) {
                localStorage.removeItem('userId');
                localStorage.removeItem('authToken');
                navigate('/login');
                return;
            }

            setLoading(true);
            setError(null);

            try {
                // 3. Отримуємо дані користувача
                const userResponse = await api.get(`/users?id=${userId}`);
                setUser(userResponse.data);

                // 4. Отримуємо бронювання користувача
                const bookingsResponse = await api.get(`/bookings?userId=${userId}`);

                // 5. Для кожного бронювання отримуємо дані про рейс
                const bookingsWithFlights = await Promise.all(
                    bookingsResponse.data.map(async (booking: Booking) => {
                        try {
                            const flightResponse = await api.get(`/flights/${booking.flightId}`);
                            return { ...booking, flight: flightResponse.data };
                        } catch (flightError) {
                            console.error(`Error fetching flight ${booking.flightId}:`, flightError);
                            return { ...booking, flight: undefined };
                        }
                    })
                );

                setBookings(bookingsWithFlights);
            } catch (error: any) {
                console.error('Error fetching profile data:', error);
                setError(error.response?.data?.message || 'Failed to load profile data');

                if (error.response?.status === 401) {
                    localStorage.removeItem('userId');
                    localStorage.removeItem('authToken');
                    navigate('/login');
                }
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [navigate]);

    if (loading) return <div className="loading-container">Loading profile...</div>;
    if (error) return <div className="error-container">{error}</div>;
    if (!user) return <div className="error-container">User not found</div>;

    // Функция для форматирования даты
    const formatDate = (dateString: string | undefined) => {
        if (!dateString) return 'Date not available';
        try {
            return new Date(dateString).toLocaleString();
        } catch {
            return 'Invalid Date';
        }
    };

    return (
        <div className="profile-page">
            <h1>My Profile</h1>
            <div className="profile-info">
                <h2>{user.fullName}</h2>
                <p>{user.email}</p>
            </div>

            <h2>My Bookings</h2>
            {bookings.length === 0 ? (
                <p>You don't have any bookings yet</p>
            ) : (
                <ul className="bookings-list">
                    {bookings.map(booking => (
                        <li key={booking.id} className="booking-item">
                            <div className="booking-header">
                                <span>Booking #{booking.id}</span>
                                {booking.status !== 'PENDING' && (
                                    <span className={`booking-status status-${booking.status.toLowerCase()}`}>
                                        {booking.status}
                                    </span>
                                )}
                            </div>

                            <div className="booking-details">
                                {booking.flight ? (
                                    <>
                                        <div>Flight: {booking.flight.flightNumber}</div>
                                        <div>From: {booking.flight.departureAirport}</div>
                                        <div>To: {booking.flight.arrivalAirport}</div>
                                        <div>Date: {formatDate(booking.flight.departureTime?.toString())}</div>
                                    </>
                                ) : (
                                    <div>Flight ID: {booking.flightId}</div>
                                )}
                                <div>Priority Boarding: {booking.priorityBoarding ? 'Yes' : 'No'}</div>
                                <div>Baggage: {booking.baggageCount} items</div>
                                <div className="booking-price">
                                    Total: {booking.totalPrice.toFixed(2)} UAH
                                </div>
                            </div>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default ProfilePage;
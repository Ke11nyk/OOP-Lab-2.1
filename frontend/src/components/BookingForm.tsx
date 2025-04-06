import { useState, useEffect } from 'react';
import api from '../api/client';
import { useNavigate } from 'react-router-dom';

interface BookingFormProps {
    flightId: number;
}

const BookingForm = ({ flightId }: BookingFormProps) => {
    const [priority, setPriority] = useState(false);
    const [baggage, setBaggage] = useState(0);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [userId, setUserId] = useState<number | null>(null);
    const navigate = useNavigate();

    // Отримуємо userId з localStorage при завантаженні компонента
    useEffect(() => {
        const storedUserId = localStorage.getItem('userId');
        if (!storedUserId) {
            // Якщо користувач не авторизований, перенаправляємо на сторінку входу
            navigate('/login?redirect=booking');
            return;
        }
        setUserId(parseInt(storedUserId));
    }, [navigate]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        // Перевірка, чи користувач авторизований
        if (!userId) {
            setError('To make a reservation you must log in.');
            navigate('/login?redirect=booking');
            return;
        }

        setIsSubmitting(true);
        setError(null);

        try {
            const response = await api.post('/bookings', {
                flightId: flightId,
                priorityBoarding: priority,
                checkedBaggage: baggage > 0,
                baggageCount: baggage,
                userId: userId
            });
            alert(`Reservation created! Reference: ${response.data.bookingReference}`);
        } catch (error: any) {
            console.error('Booking error:', error);
            setError(error.response?.data?.error || 'Reservation failed. Please try again.');
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="booking-form">
            {error && <div className="error-message">{error}</div>}

            <label>
                Priority boarding:
                <input
                    type="checkbox"
                    checked={priority}
                    onChange={() => setPriority(!priority)}
                />
            </label>

            <label>
                Baggage (maximum 3):
                <input
                    type="number"
                    min="0"
                    max="3"
                    value={baggage}
                    onChange={(e) => setBaggage(parseInt(e.target.value) || 0)}
                />
            </label>

            <button type="submit" disabled={isSubmitting || !userId}>
                {isSubmitting ? 'Processing...' : 'Book a flight'}
            </button>
        </form>
    );
};

export default BookingForm;
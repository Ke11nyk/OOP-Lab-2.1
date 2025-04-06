import { useState } from 'react';
import api from '../api/client';

interface BookingFormProps {
    flightId: number;
    userId?: number; // Add userId as prop
}

const BookingForm = ({ flightId, userId = 1 }: BookingFormProps) => {
    const [priority, setPriority] = useState(false);
    const [baggage, setBaggage] = useState(0);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);
        setError(null);

        try {
            const response = await api.post('/bookings', {
                flightId: flightId,
                priorityBoarding: priority,
                checkedBaggage: baggage > 0,
                baggageCount: baggage,
                userId: 0
            });
            alert(`Booking created! Reference: ${response.data.bookingReference}`);
        } catch (error: any) {
            console.error('Booking failed:', error);
            setError(error.response?.data?.error || 'Booking failed. Please try again.');
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="booking-form">
            {error && <div className="error-message">{error}</div>}

            <label>
                Priority Boarding:
                <input
                    type="checkbox"
                    checked={priority}
                    onChange={() => setPriority(!priority)}
                />
            </label>

            <label>
                Baggage (max 3):
                <input
                    type="number"
                    min="0"
                    max="3"
                    value={baggage}
                    onChange={(e) => setBaggage(parseInt(e.target.value) || 0)}
                />
            </label>

            <button type="submit" disabled={isSubmitting}>
                {isSubmitting ? 'Processing...' : 'Book Flight'}
            </button>
        </form>
    );
};

export default BookingForm;
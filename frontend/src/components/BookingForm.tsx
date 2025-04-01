import { useState } from 'react';
import api from '../api/client';

interface BookingFormProps {
    flightId: number;
}

const BookingForm = ({ flightId }: BookingFormProps) => {
    const [priority, setPriority] = useState(false);
    const [baggage, setBaggage] = useState(0);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);

        try {
            const response = await api.post('/bookings', {
                flightId,
                priorityBoarding: priority,
                baggageCount: baggage
            });
            alert(`Booking created! Number: ${response.data.bookingNumber}`);
        } catch (error) {
            console.error('Booking failed:', error);
            alert('Booking failed. Please try again.');
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="booking-form">
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
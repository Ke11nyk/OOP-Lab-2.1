export interface Flight {
    id: number;
    flightNumber: string;
    departureAirport: string;
    arrivalAirport: string;
    departureTime: string;
    arrivalTime: string;
    currentPrice: number;
    availableSeats: number;
}

export interface Booking {
    id: number;
    flightId: number;
    userId: string;
    bookingNumber: string;
    totalPrice: number;
    status: 'PENDING' | 'CONFIRMED' | 'CANCELLED';
    priorityBoarding: boolean;
    baggageCount: number;
}

export interface User {
    id: string;
    email: string;
    fullName: string;
}
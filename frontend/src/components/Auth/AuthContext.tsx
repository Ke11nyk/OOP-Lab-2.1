import React, { createContext, useContext, useState, useEffect } from 'react';

interface AuthContextType {
    isAuthenticated: boolean;
    userData: { id: string; name: string } | null;
    login: (id: string, name: string) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [userData, setUserData] = useState<{ id: string; name: string } | null>(null);

    useEffect(() => {
        // Check if user is authenticated on component mount
        const authToken = localStorage.getItem('authToken');
        const userId = localStorage.getItem('userId');
        const userName = localStorage.getItem('userName');

        if (authToken && userId && userName) {
            setIsAuthenticated(true);
            setUserData({ id: userId, name: userName });
        }
    }, []);

    const login = (id: string, name: string) => {
        localStorage.setItem('authToken', 'true');
        localStorage.setItem('userId', id);
        localStorage.setItem('userName', name);
        setIsAuthenticated(true);
        setUserData({ id, name });
    };

    const logout = () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('userId');
        localStorage.removeItem('userName');
        setIsAuthenticated(false);
        setUserData(null);
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, userData, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
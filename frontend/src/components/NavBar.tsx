import { Link } from 'react-router-dom';
import './NavBar.css';
import { useAuth } from './Auth/AuthContext';

const NavBar = () => {
    const { isAuthenticated, userData, logout } = useAuth();

    return (
        <nav className="navbar">
            <Link to="/" className="logo">LowCost Airlines</Link>
            <div className="nav-links">
                <Link to="/flights">Flights</Link>

                {isAuthenticated ? (
                    <div className="user-section">
                        <span>Welcome, {userData?.name}</span>
                        <button className="logout-button" onClick={logout}>Log out</button>
                    </div>
                ) : (
                    <div className="auth-links">
                        <Link to="/login">Log in</Link>
                        <Link to="/register">Register</Link>
                    </div>
                )}
            </div>
        </nav>
    );
};

export default NavBar;